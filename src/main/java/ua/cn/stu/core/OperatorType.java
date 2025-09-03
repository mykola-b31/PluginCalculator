package ua.cn.stu.core;

public enum OperatorType {
    BINARY("binary"), UNARY("unary");
    private String operatorTypeString;

    OperatorType(String operatorTypeString) {
        this.operatorTypeString = operatorTypeString;
    }

    public String getOperatorType() {
        return operatorTypeString;
    }
}
