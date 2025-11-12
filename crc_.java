import java.util.Scanner;

class CRCScanner {
    
    // --- Utility method for CRC Division (XOR) ---
    // The core XOR division logic remains necessary and cannot be significantly shortened.
    static int[] divide(int div[], int divisor[], int rem[]) {
        int cur = 0;
        while (true) {
            for (int j = 0; j < divisor.length; j++)
                rem[cur + j] = (rem[cur + j] ^ divisor[j]);
            
            while (rem[cur] == 0 && cur < rem.length - 1)
                cur++;
            
            if ((rem.length - cur) < divisor.length)
                break;
        }
        return rem;
    }

    public static void main(String args[]) {
        // BufferedReader br = new BufferedReader(new InputStreamReader(System.in)); // Deleted: Old input handler
        Scanner scanner = new Scanner(System.in);
        
        // Deleted: Separate declarations for data[], div[], rem[], crc[]
        int[] data, div, divisor, rem, crcCode;
        int dataBits, divisorBits, totalLength;
        
        System.out.println("Enter number of data bits:");
        dataBits = scanner.nextInt(); // Shorter input reading
        data = new int[dataBits];
        System.out.println("Enter data bits (0 or 1):");
        for (int i = 0; i < dataBits; i++)
            data[i] = scanner.nextInt(); // Shorter input reading
        
        System.out.println("Enter number of bits in divisor:");
        divisorBits = scanner.nextInt();
        divisor = new int[divisorBits];
        System.out.println("Enter Divisor bits (0 or 1):");
        for (int i = 0; i < divisorBits; i++)
            divisor[i] = scanner.nextInt();

        // 1. Prepare Dividend
        totalLength = dataBits + divisorBits - 1;
        div = new int[totalLength];
        rem = new int[totalLength];
        crcCode = new int[totalLength]; // Consolidated array declarations
        
        // Copy data into the dividend array, leaving space for the remainder
        // for (int i = 0; i < data.length; i++) div[i] = data[i]; // Replaced by System.arraycopy
        System.arraycopy(data, 0, div, 0, dataBits); 
        
        System.out.print("Dividend (after appending 0's) are: ");
        for (int j = 0; j < div.length; j++)
            System.out.print(div[j]);
        System.out.println();
        
        // 2. Generation of CRC (Encoding)
        // for (int j = 0; j < div.length; j++) { rem[j] = div[j]; } // Replaced by System.arraycopy
        System.arraycopy(div, 0, rem, 0, totalLength); // Copy dividend to remainder array for division
        
        rem = divide(div, divisor, rem); // Perform the division
        
        System.out.print("CRC Codeword: ");
        for (int i = 0; i < totalLength; i++) {
            crcCode[i] = (div[i] ^ rem[i]); // Construct the final CRC Codeword (Data + Remainder)
            System.out.print(crcCode[i]);
        }
        System.out.println();
        
        // --- 3. Verification of CRC (Decoding) ---
        System.out.println("Enter CRC code of " + totalLength + " bits:");
        for (int i = 0; i < totalLength; i++)
            // crc[i] = Integer.parseInt(br.readLine()); // Replaced by scanner.nextInt()
            crcCode[i] = scanner.nextInt();
        
        // for (int j = 0; j < crc.length; j++) { rem[j] = crc[j]; } // Replaced by System.arraycopy
        System.arraycopy(crcCode, 0, rem, 0, totalLength); // Copy the received codeword for re-division
        
        rem = divide(crcCode, divisor, rem); // Perform re-division
        
        // Simplified error checking loop
        boolean error = false;
        // for (int j = 0; j < rem.length; j++) { if (rem[j] != 0) { ... } if (j == rem.length - 1) { ... } } // Replaced by for-each
        for (int bit : rem) {
            if (bit != 0) {
                error = true;
                break;
            }
        }
        
        if (error) {
            System.out.println("Error Detected");
        } else {
            System.out.println("No Error");
        }

        scanner.close(); // Clean up scanner
    }
}
