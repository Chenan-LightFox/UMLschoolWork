package model;

public class BankAccount {
    private final String accountId;
    private final String cardNumber;
    private final String pin;
    private double balance;
    private boolean locked;

    public BankAccount(String accountId, String cardNumber, String pin, double balance) {
        this.accountId = accountId;
        this.cardNumber = cardNumber;
        this.pin = pin;
        this.balance = balance;
        this.locked = false;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public boolean isLocked() {
        return locked;
    }

    public void lock() {
        this.locked = true;
    }

    public boolean verifyPin(String inputPin) {
        return pin.equals(inputPin);
    }

    public double getBalance() {
        return balance;
    }

    public void debit(double amount) {
        this.balance -= amount;
    }
}
