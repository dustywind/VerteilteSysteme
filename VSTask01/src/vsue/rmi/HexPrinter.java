package vsue.rmi;

public class HexPrinter {
    
    private int bytesPerRow = 8;
    private static final int MIN_BYTES_PER_ROW = 1;
    
    private boolean printRowNumber = true;
    private final String SEPARATOR = "\t\t";
    private final char NON_READABLE_CHAR_REPLACEMENT = '.';
    
    public HexPrinter(){};
    
    public HexPrinter(int bytesPerRow){
        if(bytesPerRow < MIN_BYTES_PER_ROW){
            throw new IllegalArgumentException(
                String.format("parameter bytesPerRow must bu greater or equal %d", MIN_BYTES_PER_ROW)
            );
        }
        this.bytesPerRow = bytesPerRow;
    }
    
    public void setPrintRowNumber(boolean shouldPrint){
        printRowNumber = shouldPrint;
    }
    
    public boolean getPrintRowNumber(){
        return printRowNumber;
    }

    public void print(byte[] buffer){
        String msg = hexifyBuffer(buffer);
        System.out.println(msg);
    }
    
    private String hexifyBuffer(byte[] buffer){
        StringBuilder sb = new StringBuilder();
        
        int index = 0;
        
        int rowNumber = 1;
        while(index < buffer.length){
            
            if(printRowNumber){
                sb.append(String.format("%04d%s", rowNumber, this.SEPARATOR));
            }
            
            int bytesToPrint = Math.min(bytesPerRow, buffer.length - index);
            
            String hexifiedRow = hexifyRow(buffer, index, bytesToPrint);
            
            sb.append(hexifiedRow);
            sb.append(System.lineSeparator());
            
            index += bytesPerRow;
            rowNumber += 1;
        }
        
        return sb.toString();
    }
    
    private String hexifyRow(byte[] buffer, int start, int length){
        StringBuilder sb = new StringBuilder();
        
        for(int i = start; i < start + length; ++i){
            if(i != start){
                sb.append(' ');
            }
            String hex = String.format("%02X", buffer[i]);
            
            sb.append(hex);
        }
        
        sb.append(SEPARATOR);
        
        String msg = createReadableString(buffer, start, length);
        sb.append(msg);
        
        return sb.toString();
    }
    
    private String createReadableString(byte[] buffer, int start, int length){
        
        byte[] readable = new byte[length];
        
        for(int i = 0; i < length; ++i){
            
            byte current = buffer[start + i];
            if(isReadableCharacter(current)){

                readable[i] = current;
            }
            else{
                readable[i] = NON_READABLE_CHAR_REPLACEMENT;
            }
        }
        return new String(readable);
    }
    
    private static boolean isReadableCharacter(byte b){
        return b >= 32 && b <= 126;
    }
    
    
}
