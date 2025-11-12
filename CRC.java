import java.io.*;

class CRC {
    public static void main(String args[]) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        int[] data;
        int[] divisor;
        int[] dividend;
        int[] remainder;
        int[] crc;
        int data_bits, divisor_bits, tot_length;

        System.out.print("Enter number of data bits: ");
        data_bits = Integer.parseInt(br.readLine());
        data = new int[data_bits];

        System.out.println("Enter data bits:");
        for (int i = 0; i < data_bits; i++)
            data[i] = Integer.parseInt(br.readLine());

        System.out.print("Enter number of bits in divisor: ");
        divisor_bits = Integer.parseInt(br.readLine());
        divisor = new int[divisor_bits];

        System.out.println("Enter divisor bits:");
        for (int i = 0; i < divisor_bits; i++)
            divisor[i] = Integer.parseInt(br.readLine());

        tot_length = data_bits + divisor_bits - 1;
        dividend = new int[tot_length];
        remainder = new int[tot_length];

        // Append zeros to data
        for (int i = 0; i < data_bits; i++)
            dividend[i] = data[i];

        System.out.print("Dividend (after appending 0's): ");
        for (int j = 0; j < tot_length; j++)
            System.out.print(dividend[j]);
        System.out.println();

        // Copy dividend to remainder and divide
        for (int j = 0; j < tot_length; j++)
            remainder[j] = dividend[j];

        remainder = divide(dividend, divisor, remainder);

        // Form the CRC code
        crc = new int[tot_length];
        for (int i = 0; i < data_bits; i++)
            crc[i] = data[i];
        for (int i = data_bits; i < tot_length; i++)
            crc[i] = remainder[i];

        System.out.print("\nCRC code: ");
        for (int i = 0; i < tot_length; i++)
            System.out.print(crc[i]);
        System.out.println();

        // Check for error
        System.out.println("\nEnter received CRC code of " + tot_length + " bits:");
        for (int i = 0; i < tot_length; i++)
            crc[i] = Integer.parseInt(br.readLine());

        for (int j = 0; j < tot_length; j++)
            remainder[j] = crc[j];

        remainder = divide(crc, divisor, remainder);

        boolean error = false;
        for (int j = 0; j < tot_length; j++) {
            if (remainder[j] != 0) {
                error = true;
                break;
            }
        }

        if (error)
            System.out.println("Error detected in received code!");
        else
            System.out.println("No error detected.");
    }

    static int[] divide(int div[], int divisor[], int rem[]) {
        int cur = 0;
        while ((div.length - cur) >= divisor.length) {
            if (rem[cur] == 1) {
                for (int j = 0; j < divisor.length; j++)
                    rem[cur + j] ^= divisor[j];
            }
            cur++;
        }
        return rem;
    }
}

