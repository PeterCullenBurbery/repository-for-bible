package com.peter_burbery.simple_program;

import java.nio.ByteBuffer;
import java.util.UUID;

public class Function_Repository {

    // Function to convert Oracle RAW(16) UUID to Java UUID format
    public static UUID convertOracleUUIDToJavaUUID(String oracleUUID) {
        String formattedUUID = oracleUUID.replaceFirst(
            "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)",
            "$1-$2-$3-$4-$5"
        );
        return UUID.fromString(formattedUUID);
    }

    // Converts Java UUID to byte array for MySQL BINARY(16) storage
    public static byte[] convertUUIDToBytes(UUID uuid) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[16]);
        byteBuffer.putLong(uuid.getMostSignificantBits());
        byteBuffer.putLong(uuid.getLeastSignificantBits());
        return byteBuffer.array();
    }

 // Converts a UUID string (without dashes) to RAW(16) format for Oracle
    public static byte[] convertUUIDToRaw(String uuidString) {
        UUID uuid = Function_Repository.convertOracleUUIDToJavaUUID(uuidString);
        return Function_Repository.convertUUIDToBytes(uuid);
    }

    
    // Converts byte array from MySQL BINARY(16) back to Java UUID
    public static UUID convertBytesToUUID(byte[] bytes) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        long mostSigBits = byteBuffer.getLong();
        long leastSigBits = byteBuffer.getLong();
        return new UUID(mostSigBits, leastSigBits);
    }

    public static void main(String[] args) {
        // Example usage of UUID conversion from Oracle RAW(16)
        String exampleOracleUUID = "368E3C6209E34C20B5CBB51C6045D21A";
        UUID convertedUUID = convertOracleUUIDToJavaUUID(exampleOracleUUID);
        System.out.println("Example conversion from Oracle RAW(16): " + exampleOracleUUID + " -> " + convertedUUID);

        // Example usage of UUID to byte array conversion (for MySQL)
        byte[] uuidBytes = convertUUIDToBytes(convertedUUID);
        System.out.print("UUID as byte array: ");
        for (byte b : uuidBytes) {
            System.out.printf("%02x ", b);
        }
        System.out.println();

        // Example usage of byte array to UUID conversion (retrieving from MySQL BINARY(16))
        UUID recoveredUUID = convertBytesToUUID(uuidBytes);
        System.out.println("Recovered UUID from bytes: " + recoveredUUID);
    }
}
