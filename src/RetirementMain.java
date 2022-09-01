import java.util.*;


public class RetirementMain {
    
    static Person p1;
    static int years;
    static double savingsAfterInv;
    static double incAfterTax;

    public static void main(String[] args) throws Exception {
        prompt();

        double r = roth();
        double t = traditional();

        Scanner inp = new Scanner(System.in);
        System.out.print("\nWould you like to use your Roth (r) 401k retirement fund or Traditional (t) 401k retirement fund for the rest of this simulation? ");
        while (true) {
            char choice = inp.next().charAt(0);
            if (choice == 'r') {
                afterRet(r, true);
                break;
            } else if (choice == 't') {
                afterRet(t, false);
                break;
            } else {
                System.out.println("Please enter valid input.");
            }
        }  
    }

    public static double diff(double inc, double inv, double tax) {
        double roth = inc - (inc * tax / 100);
        roth = roth - inv;

        double trad = inc - inv;
        trad = trad - (trad * tax / 100);

        return trad - roth;
    }   

    public static void prompt() {
        Scanner inp = new Scanner(System.in);
        System.out.println("\nThis program will take in certain parameters from the user and run the calculations for a 401k investment\nportfolio and a Roth 401k investment portfolio.\n");
        System.out.print("How much are you initially starting your retirement fund with: ");
        int dep = inp.nextInt();
        System.out.print("Please enter your income per year: ");
        int income = inp.nextInt();
        System.out.print("Please enter your contribution to your retirement fund every year: ");
        int contribution = inp.nextInt();
        System.out.print("Please enter the rate of contribution of your employer with respect to your contribution as a percent: ");
        int empCont = inp.nextInt();
        System.out.print("Please enter your current age: ");
        int age = inp.nextInt();
        System.out.print("Please enter your planned retirement age: ");
        int retAge = inp.nextInt();
        System.out.print("We will be using the actual current tax rates.\nPlease enter the expected retirement tax rate as a percentage: ");
        double expTaxRate = inp.nextDouble();
        System.out.print("Please enter the investment growth rate as a percent: ");
        double growthRate = inp.nextDouble();

        p1 = new Person(dep, income, contribution, empCont, age, retAge, expTaxRate, growthRate);
    }

    public static void afterRet(double fund, boolean roth) {
        System.out.println("\nYour age is now " + p1.retAge + " and you have retired with $" + fund + " in your selected fund. How much would you like to keep investing in your retirement fund yearly?");
        Scanner inp = new Scanner(System.in);
        double inv = inp.nextInt();

        boolean stop = false;
        int age = p1.retAge;
        double init = fund;
        while (true) {
            System.out.println("How many years would you like to simulate forward or end (0) simulation?");
            int sim = inp.nextInt();
            if (sim == 0) {
                break;
            }
            age = age + sim;

            if (!stop) {
                if (roth) {
                    inv = inv - ((p1.expTaxRate / 100) * inv);
                    fund = ((fund + (inv / (p1.growthRate / 100))) * Math.pow((1 + (p1.growthRate) / 100), sim)) - (inv / (p1.growthRate / 100));
                } else {
                    fund = ((fund + (inv / (p1.growthRate / 100))) * Math.pow((1 + (p1.growthRate) / 100), sim)) - (inv / (p1.growthRate / 100));
                    System.out.println(fund);
                    double diff = fund - init;
                    diff = diff - ((p1.expTaxRate / 100) * diff);
                    System.out.print(diff);
                    fund = diff + init;
                    init = fund;

                }
                int b = (int)(fund * 100);
                fund = b / 100;
            } else {
                fund = ((fund) * Math.pow((1 + (p1.growthRate) / 100), sim));
            }
            
            while (true) {
                System.out.print("You are now " + age + " years old and your balance is $" + fund + ". Would you like to withdraw (w) money, stop (s) depositing money, or exit (e)? ");
                char decision = inp.next().charAt(0);
                if (decision == 'w') {
                    System.out.println("Your balance is $" + fund + ". How much would you like to withdraw?");
                    int amt = inp.nextInt();
                    fund = fund - amt;
                    System.out.println("Your updated balance is $" + fund +".");
                } else if (decision == 's') {
                    stop = true;
                } else if (decision == 'e') {
                    break;
                } else {
                    System.out.println("Please enter a valid input.");
                }
            }
            
            
        }
        System.out.println("Program end");
    }


    public static double traditional() {
        years = p1.retAge - p1.age;

        //income is taxed after contribution
        double incAfterInv = p1.income - p1.contribution;
        incAfterTax = incAfterInv - calculateCurrentTax(incAfterInv);
        System.out.println("\nTraditional 401k --------\nYou are paying $" + calculateCurrentTax(incAfterInv) + " in tax yearly.");
        //incAfterTax = incAfterInv - (0.25 * incAfterInv);

        //just savings that have not been put into any fund or stocks
        double savings = incAfterTax * years;
        double diff = incAfterTax - savingsAfterInv;

        //lets say the difference we saved is invested into a steadily growing s&p
        double sp = ((diff + (diff / (0.07))) * Math.pow((1 + (0.07)), years)) - (diff / (0.07));
        int ac = (int)(sp * 100);
        sp  = ac / 100;
        System.out.println("If you were to invest the difference saved ($" + diff + ") yearly by paying tax later into a S&P 500, with a standard growth rate of 7% over the last 20 years, this would earn you $" + sp + ".");


        double empMatch =  p1.contribution * p1.empCont * 0.01;
        double yearlyInv = p1.contribution + empMatch;

        double rFund = ((p1.deposit + (yearlyInv / (p1.growthRate / 100))) * Math.pow((1 + (p1.growthRate) / 100), years)) - (yearlyInv / (p1.growthRate / 100));
        rFund = rFund - ((p1.expTaxRate / 100) * rFund);
        int a = (int)(rFund * 100);
        rFund  = a / 100;
        System.out.println("When you retire, you will have $" + rFund + " in your Traditional 401k retirement fund after tax.");
        

        return rFund;
    }

    public static double roth() {
        years = p1.retAge - p1.age;

        //contributions are taxed when earned
        double taxedInc = p1.income - calculateCurrentTax(p1.income);
        System.out.println("\nRoth 401k --------\nYou are paying $" + calculateCurrentTax(p1.income) + " in tax yearly.");
        savingsAfterInv = taxedInc - p1.contribution;

        //just checkings/savings that have not been put into any fund or stocks
        double savings = savingsAfterInv * years;

        double empMatch = p1.contribution * p1.empCont * 0.01;
        double yearlyInv = p1.contribution + empMatch;

        //retirement fund when you retire
        double rFund = ((p1.deposit + (yearlyInv / (p1.growthRate / 100))) * Math.pow((1 + (p1.growthRate) / 100), years)) - (yearlyInv / (p1.growthRate / 100));
        int a = (int)(rFund * 100);
        rFund  = a / 100;
        System.out.println("When you retire, you will have $" + rFund + " in your Roth 401k retirement fund. You will not have to pay tax on this.");

        return rFund;
    }

    public static double calculateCurrentTax(double income) {
        if (income <= 9950) {
            return income * 0.1;
        } else if (9951 <= income && income <= 40525) {
            return 995 + ((income - 9950) * 0.12);
        } else if (40526 <= income && income <= 86375) {
            return 4664 + ((income - 40525) * 0.22);
        } else if (86376 <= income && income <= 164925) {
            return 14751 + ((income - 86375) * 0.24);
        } else if (164926 <= income && income <= 209425) {
            return 33603 + ((income - 164925) * 0.32);
        } else if (209426 <= income && income <= 523600) {
            return 47843 + ((income - 209425) * 0.35);
        } else {
            return 157804.25 + ((income - 523600) * 0.37);
        }
     }
}


/**
 * 401k vs roth 401k
 * 
 * parameters: 
 * - income/year
 * - contribution/year
 * - employment contribution rate/year
 * - current age
 * - retirement age
 * - current tax rate
 * - expected retirement tax rate
 * - investment growth rate
 * - withdrawal age
 * 
 * - current age to retirement age will have constant deposits, no withdrawals 
 * - from retirement age onwards, deposits and withdrawals
 */