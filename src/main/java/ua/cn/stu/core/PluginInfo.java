package ua.cn.stu.core;

public class PluginInfo {

    private Class<?> classReference;
    private OperatorType operatorType;
    private String operator;
    private String description;

    public Class<?> getClassReference() {
        return classReference;
    }

    public void setClassReference(Class<?> classReference) {
        this.classReference = classReference;
    }

    public OperatorType getOperatorType() {
        return operatorType;
    }

    public void setOperatorType(OperatorType operatorType) {
        this.operatorType = operatorType;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
