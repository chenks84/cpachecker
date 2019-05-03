import json
import os
import re
import sys
import textwrap
from pathlib import Path

import esprima
import yaml
from esprima.tokenizer import BufferEntry

try:
    from StringIO import StringIO
except ImportError:
    from io import StringIO


def parse_yaml_string(ys):
    fd = StringIO(ys)
    dct = yaml.safe_load(fd)
    return dct


def get_meta_data(file, file_content):
    meta_data_match = re.search(r'/\*---(.+?)---\*/', file_content, re.DOTALL)
    if meta_data_match is None:
        eprint('meta data not found in file {}'.format(file))
        return {'flags': [], 'features': []}
    meta_data = parse_yaml_string(meta_data_match.group(1))
    if 'flags' not in meta_data:
        meta_data['flags'] = []
    if 'features' not in meta_data:
        meta_data['features'] = []
    return meta_data


class bcolors:
    HEADER = '\033[95m'
    OKBLUE = '\033[94m'
    OKGREEN = '\033[92m'
    WARNING = '\033[93m'
    FAIL = '\033[91m'
    ENDC = '\033[0m'
    BOLD = '\033[1m'
    UNDERLINE = '\033[4m'


def eprint(*args, **kwargs):
    print(bcolors.WARNING, *args, bcolors.ENDC, file=sys.stderr, **kwargs)


class Token:
    def __init__(self, buffer_entry: BufferEntry):
        self.bufferEntry = buffer_entry

    def __eq__(self, other):
        return (self.bufferEntry.type == other.bufferEntry.type and
                self.bufferEntry.value == other.bufferEntry.value and
                self.bufferEntry.regex == other.bufferEntry.regex and
                self.bufferEntry.range == other.bufferEntry.range and
                self.bufferEntry.loc == other.bufferEntry.loc)

    def __str__(self):
        return str(self.bufferEntry)

    def __repr__(self):
        return str(self.bufferEntry)


def tokenize(program):
    return list(map(Token, esprima.tokenize(program)))


def contains_subsequence(subsequence, sequence):
    l = len(subsequence)
    return any(subsequence == sequence[i:i + l] for i in range(len(sequence) - l + 1))


def contains_for_in_statement(file_content):
    return re.search('\\s+for\\s*\\([^)]+in', file_content)


def contains_with_statement(file_content):
    return re.search('\\s+with\\s*\\([^)]+\\)\\s*{', file_content)


def is_skip_directory(dir):
    """
    Return if directory should be skipped (contains only files with unsupported features)
    :type dir: Path
    :return:
    """
    root = project_root_dir / 'test/programs/javascript-test262-benchmark/test/language/'
    skipped_directories = [
        'expressions/arrow-function',
        'expressions/async-arrow-function',
        'expressions/async-function',
        'expressions/async-generator',
        'expressions/await',
        'expressions/class',
        'expressions/concatenation',  # support of string concatenation to restricted yet
        'expressions/dynamic-import',
        'expressions/generators',
        'expressions/exponentiation',
        'expressions/in',  # TODO in operator
        'expressions/instanceof',  # TODO instanceof operator
        'expressions/template-literal',
        'expressions/tagged-template',
        'literals/regexp',
        'statements/async-function',
        'statements/async-generator',
        'statements/class',
        'statements/for-await-of',
        'statements/for-in',
        'statements/for-of',
        'statements/generators',
        'statements/with',
    ]
    return any(dir == (root / sub_dir) or (root / sub_dir) in dir.parents for sub_dir in skipped_directories)


def contains_assertion(file_content):
    assertion_sub_strings = [
        'assert(',
        'assert.sameValue(',
    ]
    return any(s in file_content for s in assertion_sub_strings)


class UnsupportedFeatureVisitor(esprima.NodeVisitor):
    def __init__(self):
        self.has_unsupported_feature = False
        self.found_unsupported_features = set()
        self.node_types = set()

    def visit_Object(self, obj):
        # print(obj.type)
        self.node_types.add(obj.type)
        unsupported_node_types = [
            'ArrayPattern',
            'ArrowFunctionExpression',
            'AssignmentPattern',
            'AwaitExpression',
            'ClassDeclaration',
            'ForInStatement',
            'ForOfStatement',
            'Import',
            'ObjectPattern',
            'RestElement',
            'SpreadElement',
            'Super',
            'TemplateLiteral',
            'ThrowStatement',
            'TryStatement',
            'WithStatement',
            'YieldExpression',
        ]
        if obj.type in unsupported_node_types:
            self.found_unsupported_features.add(obj.type)
            self.has_unsupported_feature = True
        if obj.type == 'BinaryExpression' and obj.operator in ['in', 'instanceof']:
            self.found_unsupported_features.add(obj.operator + ' operator')
            self.has_unsupported_feature = True
        if obj.type == 'CallExpression' and obj.callee.type == 'Identifier':
            unsupported_callee_names = [
                # 'Array',
                # 'Boolean',
                # 'Date',
                'Function',
                # 'Number',
                # 'Object',
                # 'String',
            ]
            if obj.callee.name in unsupported_callee_names:
                self.found_unsupported_features.add('call of ' + obj.callee.name)
                self.has_unsupported_feature = True
        if (obj.type in ['FunctionDeclaration', 'FunctionExpression']
                and (obj.generator or obj.isAsync)):
            self.found_unsupported_features.add(obj.type)
            self.has_unsupported_feature = True
        # property names that indicate unsupported feature
        unsupported_identifiers = [
            'arguments',
            'eval',
            'Math',
            'Promise',
            'RegExp',
            'ReferenceError',
            'Symbol',
        ]
        if obj.type == 'Identifier' and obj.name in unsupported_identifiers:
            self.found_unsupported_features.add(obj.name)
            self.has_unsupported_feature = True
        if obj.type == 'MemberExpression' and obj.property.type == 'Identifier':
            if (obj.property.name == 'keys'
                    and obj.object.type == 'Identifier'
                    and obj.object.name == 'Object'):
                self.found_unsupported_features.add(obj.property.name)
                self.has_unsupported_feature = True
            # property names that indicate unsupported feature
            unsupported_property_names = [
                'apply',
                'bind',
                'call',
                'charCodeAt',
                'constructor',
                'defineProperty',
                'fromCharCode',
                'getOwnPropertyDescriptor',
                'hasOwnProperty',
                'isPrototypeOf',
                'setPrototypeOf',
                'throws',
                'toString',
                'valueOf',
            ]
            if obj.property.name in unsupported_property_names:
                self.found_unsupported_features.add(obj.property.name)
                self.has_unsupported_feature = True
        if obj.type == 'NewExpression' and obj.callee.type == 'Identifier':
            unsupported_callee_names = [
                'Array',
                'Boolean',
                'Date',
                'Function',
                'Number',
                'Object',
                'String',
            ]
            if obj.callee.name in unsupported_callee_names:
                self.found_unsupported_features.add('new ' + obj.callee.name)
                self.has_unsupported_feature = True
        if obj.type == 'Property':
            if obj.kind in ['get', 'set']:
                self.found_unsupported_features.add(obj.kind)
                self.has_unsupported_feature = True
            if obj.method:
                self.found_unsupported_features.add('method property')
                self.has_unsupported_feature = True
            if obj.key.type == 'Identifier' and obj.key.name in ['toString', 'valueOf']:
                self.found_unsupported_features.add(obj.key.name)
                self.has_unsupported_feature = True
        if obj.type == 'UnaryExpression' and obj.operator == 'delete':
            self.found_unsupported_features.add('delete operator')
            self.has_unsupported_feature = True
        if obj.type == 'VariableDeclaration' and obj.kind in ['const', 'let']:
            self.found_unsupported_features.add(obj.kind)
            self.has_unsupported_feature = True
        return super().visit_Object(obj)


def is_skip(file, file_content):
    """
    Return if file should be skipped (contains unsupported features)
    :type file_content: str
    """
    if 'String.prototype.replace' in file_content:
        return True
    # TODO includes in meta data
    meta_data = get_meta_data(file, file_content)
    if 'es6id' in meta_data:
        return True
    unsupported_flags = [
        'async',
        'generators',
        'module',
        'noStrict',
    ]
    if any(f in meta_data['flags'] for f in unsupported_flags):
        return True
    unsupported_features = [
        'BigInt',
        'Map',
        'Proxy',
        'Reflect',
        'Reflect.construct',
        'Set',
        'Symbol',
        'Symbol.asyncIterator',
        'Symbol.hasInstance',
        'Symbol.iterator',
        'Symbol.toPrimitive',
        'Symbol.toStringTag',
        'Symbol.unscopables',
        'TypedArray',
        'arrow-function',
        'async-functions',
        'async-iteration',
        'caller',
        'class',
        'class-fields-private',
        'class-fields-public',
        'class-methods-private',
        'class-static-fields-private',
        'class-static-fields-public',
        'class-static-methods-private',
        'computed-property-names',
        'const',
        'cross-realm',
        'default-parameters',
        'destructuring-assignment',
        'destructuring-binding',
        'dynamic-import',
        'export-star-as-namespace-from-module',
        'for-of',
        'generators',
        'import.meta',
        'json-superset',
        'let',
        'new.target',
        'numeric-separator-literal',
        'object-rest',
        'object-spread',
        'optional-catch-binding',
        'regexp-named-groups',
        'super',
        'tail-call-optimization',
        'template',
        'u180e'
    ]
    if any(f in meta_data['features'] for f in unsupported_features):
        return True
    if 'negative' in meta_data:
        assert 'type' in meta_data['negative'], \
            'negative.type does not exist in meta data of {}\n{}'.format(file, json.dumps(meta_data, indent=4, sort_keys=False))
        if 'SyntaxError' in meta_data['negative']['type']:
            return True
    try:
        v = UnsupportedFeatureVisitor()
        ast = esprima.parse(file_content, delegate=v)
        # print(json.dumps(ast, indent=4, sort_keys=False))
        # print(ast)
        # print('')
        v.visit(ast)
        if v.has_unsupported_feature:
            # print('has unsupported features {}\n\t{}'.format(file, v.found_unsupported_features))
            return True
    except (esprima.error_handler.Error, RecursionError):
        eprint('could not parse {} due to {}'.format(file, sys.exc_info()[0]))
    try:
        tokens = tokenize(file_content)
    except (esprima.error_handler.Error, RecursionError):
        eprint('could not tokenize file {} due to {}'.format(file, sys.exc_info()[0]))
        tokens = list()
    # TODO elided array elements are not parsed correctly by Eclipse parser
    # https://bugs.eclipse.org/bugs/show_bug.cgi?id=544733
    return any(contains_subsequence(tokenize(c), tokens) for c in [',]', '[,', ',,'])


def create_task_file(yml_file, input_files, property_file, expected_verdict):
    input_files_formatted = "\n".join('  - "%s"' % (file_name) for file_name in input_files)
    yml_file.write_text(textwrap.dedent("""\
        format_version: "1.0"
        input_files:
        {input_files_formatted}
        properties:
          - property_file: {property_file}
            expected_verdict: {expected_verdict}
        """).format(expected_verdict=expected_verdict,
                    input_files_formatted=input_files_formatted,
                    property_file=property_file))


project_root_dir = Path(__file__).parent.parent.parent

delete_file_patterns = [
    'test/programs/javascript-test262-benchmark/test/language/**/*.yml',
    'test/programs/javascript-test262-benchmark/test/language/**/*.negated',
]
delete_error_occurred = False
for file_pattern in delete_file_patterns:
    for file in project_root_dir.glob(file_pattern):
        try:
            file.unlink()
        except OSError:
            eprint("Error while deleting file {}".format(file))
            delete_error_occurred = True
if delete_error_occurred:
    exit(1)

# Specification-Dateien im "config"-Ordner sind aus Sicherheitsgründen in der VerifierCloud
# verboten. Daher wird als Workaround auf ein anderes Verzeichnis verwiesen:
property_file = project_root_dir / 'test/config/specification/JavaScriptAssertion.spc'
# property_file = project_root_dir / 'config/specification/JavaScriptAssertion.spc'
if not property_file.exists():
    eprint('Property file {} not found'.format(property_file))
    exit(1)
error_lib_file = \
    project_root_dir / 'test/programs/javascript-test262-benchmark/CPAchecker-test262-error.js'
if not error_lib_file.exists():
    eprint('Assertion library file {} not found'.format(error_lib_file))
    exit(1)
assert_lib_file = \
    project_root_dir / 'test/programs/javascript-test262-benchmark/CPAchecker-test262-assert.js'
if not assert_lib_file.exists():
    eprint('Assertion library file {} not found'.format(assert_lib_file))
    exit(1)
assert_lib_negated_file = \
    project_root_dir / 'test/programs/javascript-test262-benchmark/CPAchecker-test262-assert-negated.js'
if not assert_lib_negated_file.exists():
    eprint('Negated assertion library file {} not found'.format(assert_lib_negated_file))
    exit(1)
std_lib_file = project_root_dir / 'contrib/javascript/std-lib.js'
if not std_lib_file.exists():
    eprint('Standard library file {} not found'.format(std_lib_file))
    exit(1)

yml_file_names = set()

# commented patterns contain files with unsupported features
file_patterns = [
    # 'test/programs/javascript-test262-benchmark/test/language/arguments-object/**/*.js',
    'test/programs/javascript-test262-benchmark/test/language/asi/**/*.js',
    # 'test/programs/javascript-test262-benchmark/test/language/block-scope/**/*.js',
    'test/programs/javascript-test262-benchmark/test/language/comments/**/*.js',
    # 'test/programs/javascript-test262-benchmark/test/language/computed-property-names/**/*.js',
    # 'test/programs/javascript-test262-benchmark/test/language/destructuring/**/*.js',
    # 'test/programs/javascript-test262-benchmark/test/language/directive-prologue/**/*.js',
    # 'test/programs/javascript-test262-benchmark/test/language/eval-code/**/*.js',
    # 'test/programs/javascript-test262-benchmark/test/language/export/**/*.js',
    'test/programs/javascript-test262-benchmark/test/language/expressions/**/*.js',
    'test/programs/javascript-test262-benchmark/test/language/function-code/**/*.js',
    'test/programs/javascript-test262-benchmark/test/language/future-reserved-words/**/*.js',
    # 'test/programs/javascript-test262-benchmark/test/language/global-code/**/*.js',
    'test/programs/javascript-test262-benchmark/test/language/identifier-resolution/**/*.js',
    'test/programs/javascript-test262-benchmark/test/language/identifiers/**/*.js',
    # 'test/programs/javascript-test262-benchmark/test/language/import/**/*.js',
    'test/programs/javascript-test262-benchmark/test/language/keywords/**/*.js',
    'test/programs/javascript-test262-benchmark/test/language/line-terminators/**/*.js',
    'test/programs/javascript-test262-benchmark/test/language/literals/**/*.js',
    # 'test/programs/javascript-test262-benchmark/test/language/module-code/**/*.js',
    'test/programs/javascript-test262-benchmark/test/language/punctuators/**/*.js',
    'test/programs/javascript-test262-benchmark/test/language/reserved-words/**/*.js',
    # 'test/programs/javascript-test262-benchmark/test/language/rest-parameters/**/*.js',
    'test/programs/javascript-test262-benchmark/test/language/source-text/**/*.js',
    'test/programs/javascript-test262-benchmark/test/language/statements/**/*.js',
    'test/programs/javascript-test262-benchmark/test/language/types/**/*.js',
    'test/programs/javascript-test262-benchmark/test/language/white-space/**/*.js',
]

for file_pattern in file_patterns:
    for file in project_root_dir.glob(file_pattern):
        relpath = lambda f: os.path.relpath(str(f), str(file.parent))
        file_content = file.read_text()
        if is_skip_directory(file.parent) or 'bigint' in file.stem or is_skip(file, file_content):
            print('SKIP {}'.format(file))
            continue
        file_contains_error_assertion_call = '$ERROR(' in file_content
        file_contains_assertion = contains_assertion(file_content)
        if not(file_contains_error_assertion_call or file_contains_assertion):
            eprint('file contains no assertion {}'.format(file))
            continue
        else:
            print('GENERATE TASK FOR {}'.format(file))
        relative_path_to_property_file = relpath(property_file)
        yml_file_name = file.stem + '.yml'
        i = 0
        while yml_file_name in yml_file_names:
            yml_file_name = '{}_{}.yml'.format(file.stem, i)
            i = i + 1
        yml_file_names.add(yml_file_name)
        yml_file = file.parent / yml_file_name
        assertion_files = [relpath(error_lib_file)]
        if file_contains_assertion:
            assertion_files.append(relpath(assert_lib_file))
        create_task_file(
            yml_file=yml_file,
            input_files=assertion_files + [relpath(std_lib_file), './' + file.name],
            property_file=relative_path_to_property_file,
            expected_verdict='true')
        # uncomment to skip creation of negated tests
        # continue
        negated_assertion_files = [relpath(error_lib_file)]
        if file_contains_assertion:
            negated_assertion_files.append(relpath(assert_lib_negated_file))
        # negated test
        if file_contains_error_assertion_call:
            # create negated task for each error case
            error_cases = re.finditer(r'(\sif\s*\()(.*?)(\)\s*{?\s*\$ERROR\()', file_content)
            for errorCaseIndex, m in enumerate(error_cases):
                # negate condition of if-statement directly before call of $ERROR
                file_content_negated = ''.join([
                    file_content[0:m.start()],
                    m.group(1),
                    '!(',
                    m.group(2),
                    ')',
                    m.group(3),
                    file_content[m.end():],
                ])
                error_case_file = file.parent / ('%s.js.%d.negated' % (file.stem, errorCaseIndex))
                print('GENERATE NEGATED JS  {}'.format(error_case_file))
                error_case_file.write_text(file_content_negated)
                yml_file_name = '{}_{}.{}_false.yml'.format(error_case_file.stem, i, errorCaseIndex)
                yml_file = \
                    error_case_file.parent / yml_file_name.replace('.js.%d' % errorCaseIndex, '')
                print('GENERATE NEGATED YML {}'.format(yml_file))
                create_task_file(
                    yml_file=yml_file,
                    input_files=negated_assertion_files + [relpath(std_lib_file), './' + error_case_file.name],
                    property_file=relative_path_to_property_file,
                    expected_verdict='false')
        else:
            yml_file_name = \
                '{}_false.yml'.format(file.stem) if i == 0 else '{}_{}_false.yml'.format(file.stem,
                                                                                         i)
            yml_file = file.parent / yml_file_name.replace('.js', '')
            create_task_file(
                yml_file=yml_file,
                input_files=negated_assertion_files + [relpath(std_lib_file), './' + file.name],
                property_file=relative_path_to_property_file,
                expected_verdict='false')
