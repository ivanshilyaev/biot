package by.bsu.biot.dto;

public enum MachineDataType {
    NULL("00000001"),
    KEY("00000101"),
    DATA("00001001"),
    TEXT("00001101"),
    OUT("00010001");

    public final String code;

    MachineDataType(String code) {
        this.code = code;
    }
}
