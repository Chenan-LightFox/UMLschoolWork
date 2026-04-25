package system;

import java.util.HashMap;
import java.util.Map;

import model.BankAccount;
import model.WithdrawalResult;

public class InMemoryBankSystem implements BankSystem {
    private static final double WITHDRAW_LIMIT = 5000;
    private static final int AMOUNT_STEP = 50;
    private static final double FEE_RATE = 0.01;

    private final Map<String, BankAccount> accountByCard = new HashMap<>();

    public InMemoryBankSystem() {
        accountByCard.put("622202001", new BankAccount("A1001", "622202001", "123456", 12000));
    }

    @Override
    public boolean authenticateCard(String cardNumber, String pin) {
        BankAccount account = accountByCard.get(cardNumber);
        if (account == null || account.isLocked()) {
            return false;
        }
        return account.verifyPin(pin);
    }

    @Override
    public boolean isCardLocked(String cardNumber) {
        BankAccount account = accountByCard.get(cardNumber);
        return account != null && account.isLocked();
    }

    @Override
    public void lockCard(String cardNumber) {
        BankAccount account = accountByCard.get(cardNumber);
        if (account != null) {
            account.lock();
        }
    }

    @Override
    public WithdrawalResult withdraw(String cardNumber, double amount) {
        BankAccount account = accountByCard.get(cardNumber);
        if (account == null) {
            return WithdrawalResult.fail("银行卡不存在");
        }
        if (account.isLocked()) {
            return WithdrawalResult.fail("卡片已锁定");
        }
        if (amount <= 0) {
            return WithdrawalResult.fail("取款金额必须大于0");
        }
        if (amount % AMOUNT_STEP != 0) {
            return WithdrawalResult.fail("取款金额必须为50的倍数");
        }
        if (amount > WITHDRAW_LIMIT) {
            return WithdrawalResult.fail("单次取款不能超过5000元");
        }

        double fee = amount * FEE_RATE;
        double totalDebit = amount + fee;
        if (account.getBalance() < totalDebit) {
            return WithdrawalResult.fail("余额不足");
        }

        account.debit(totalDebit);
        return WithdrawalResult.ok(amount, fee, account.getBalance());
    }
}
