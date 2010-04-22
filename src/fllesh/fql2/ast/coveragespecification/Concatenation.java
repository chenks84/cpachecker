package fllesh.fql2.ast.coveragespecification;

public class Concatenation implements CoverageSpecification {

  private CoverageSpecification mFirstSubspecification;
  private CoverageSpecification mSecondSubspecification;
  
  public Concatenation(CoverageSpecification pFirstSubspecification, CoverageSpecification pSecondSubspecification) {
    mFirstSubspecification = pFirstSubspecification;
    mSecondSubspecification = pSecondSubspecification;
  }
  
  public CoverageSpecification getFirstSubspecification() {
    return mFirstSubspecification;
  }
  
  public CoverageSpecification getSecondSubspecification() {
    return mSecondSubspecification;
  }
  
  @Override
  public <T> T accept(ASTVisitor<T> pVisitor) {
    return pVisitor.visit(this);
  }
  
  @Override
  public String toString() {
    return "(" + mFirstSubspecification.toString() + "." + mSecondSubspecification.toString() + ")";
  }

}
