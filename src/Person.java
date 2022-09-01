public class Person {
    
    int deposit;
    int income;
    int contribution;
    int empCont;
    int age;
    int retAge;
    double expTaxRate;
    double growthRate;

    public Person (int d, int i, int cont, int empC, int a, int retA, double expT, double gRate) {
        deposit = d;
        income = i;
        contribution = cont;
        empCont = empC;
        age = a;
        retAge = retA;
        expTaxRate = expT;
        growthRate = gRate;
    }
}
