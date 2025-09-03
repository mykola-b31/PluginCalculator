package ua.cn.stu.plugin.minusoperation;

import ua.cn.stu.plugin.api.BinaryOperator;
import ua.cn.stu.plugin.api.Plugin;

public class MinusPlugin implements Plugin, BinaryOperator {

    public static final String PLUGIN_NAME = "Minus operation plugin";

    @Override
    public double calculateBinary(double firstOperand, double secondOperand) {
        return firstOperand - secondOperand;
    }

    @Override
    public void invoke() {
        System.out.println(PLUGIN_NAME + " loaded");
    }

}
