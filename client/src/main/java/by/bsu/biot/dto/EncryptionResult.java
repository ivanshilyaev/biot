package by.bsu.biot.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EncryptionResult {
    private byte[] Y;
    private byte[] T;
}
