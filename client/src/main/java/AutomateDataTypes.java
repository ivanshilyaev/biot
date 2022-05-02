public enum AutomateDataTypes {
    NULL("000000"),
    KEY("000001"),
    DATA("000010"),
    TEXT("000011"),
    OUT("000100");

    public final String code;

    AutomateDataTypes(String code) {
        this.code = code;
    }
}
