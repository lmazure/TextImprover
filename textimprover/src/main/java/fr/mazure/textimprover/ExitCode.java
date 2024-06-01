package fr.mazure.textimprover;

public enum ExitCode {

    SUCCESS(0),
    INVALID_COMMAND_LINE(1),
    FILE_ERROR(2),
    MODEL_ERROR(3);

    private final int code;

    ExitCode(final int code) {
        this.code = code;
    }
    
    public int getCode() {
        return this.code;
    }
}
