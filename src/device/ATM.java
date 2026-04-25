package device;

import java.util.Scanner;

import model.WithdrawalResult;
import system.BankSystem;

public class ATM {
    private static final int MAX_PIN_TRIES = 3;

    private final BankSystem bankSystem;
    private final CashDispenser cashDispenser;
    private final ReceiptPrinter receiptPrinter;

    public ATM(BankSystem bankSystem, CashDispenser cashDispenser, ReceiptPrinter receiptPrinter) {
        this.bankSystem = bankSystem;
        this.cashDispenser = cashDispenser;
        this.receiptPrinter = receiptPrinter;
    }

    public void startWithdrawalFlow(Scanner scanner) {
        System.out.println("欢迎使用ATM，请插入银行卡(输入卡号):");
        String cardNumber = scanner.nextLine().trim();

        if (bankSystem.isCardLocked(cardNumber)) {
            System.out.println("该卡已被锁定，请联系银行柜台。");
            return;
        }

        boolean authPassed = false;
        for (int i = 1; i <= MAX_PIN_TRIES; i++) {
            System.out.println("请输入密码:");
            String pin = scanner.nextLine().trim();

            if (bankSystem.authenticateCard(cardNumber, pin)) {
                authPassed = true;
                break;
            }

            int remaining = MAX_PIN_TRIES - i;
            if (remaining > 0) {
                System.out.println("密码错误，请重试。剩余次数: " + remaining);
            }
        }

        if (!authPassed) {
            bankSystem.lockCard(cardNumber);
            System.out.println("密码错误已达3次，系统吞卡。交易结束。");
            return;
        }

        System.out.println("认证成功。请选择功能: 1.取款");
        String menu = scanner.nextLine().trim();
        if (!"1".equals(menu)) {
            System.out.println("仅实现了第九周作业范围: 取款功能。已退卡。");
            return;
        }

        System.out.println("请输入取款金额:");
        String amountText = scanner.nextLine().trim();
        double amount;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException ex) {
            System.out.println("金额格式非法，交易结束。");
            return;
        }

        WithdrawalResult result = bankSystem.withdraw(cardNumber, amount);
        if (!result.isSuccess()) {
            System.out.println("取款失败: " + result.getMessage());
            System.out.println("已退卡。");
            return;
        }

        cashDispenser.dispense(result.getAmount());
        System.out.println("是否打印凭条? (Y/N)");
        String printChoice = scanner.nextLine().trim();
        if ("Y".equalsIgnoreCase(printChoice)) {
            receiptPrinter.print(result);
        }
        System.out.println("请取卡，欢迎下次使用。");
    }
}
