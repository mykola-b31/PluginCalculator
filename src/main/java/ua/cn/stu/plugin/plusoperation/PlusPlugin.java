package ua.cn.stu.plugin.plusoperation;

import ua.cn.stu.plugin.api.BinaryOperator;
import ua.cn.stu.plugin.api.Plugin;

public class PlusPlugin implements Plugin, BinaryOperator {

    public static final String PLUGIN_NAME = "Plus operation plugin";

    @Override
    public void invoke() {
        System.out.println(PLUGIN_NAME + " loaded");
    }

    @Override
    public double calculateBinary(double firstOperand, double secondOperand) {
        return firstOperand + secondOperand;
    }
}
