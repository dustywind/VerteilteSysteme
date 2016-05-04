package vsue.rmi;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class VSConnection {
    
    private Socket connection;
    
    public VSConnection(Socket openConnection) {
        connection = openConnection;
    }

    public void sendChunk(byte[] chunk) throws Exception {
        
        OutputStream outStream = connection.getOutputStream();
        VSMessage vsMsg = new VSMessage(chunk);
        byte[] wrappedMsg = vsMsg.getWrappedMessage();
        outStream.write(wrappedMsg);
    }
    
    public byte[] receiveChunk() throws Exception {
        
        InputStream inStream = connection.getInputStream();

        VSMessage vsMsg = new VSMessage(inStream);
        byte[] plainMsg = vsMsg.getFullPlainMessage();
        
        return plainMsg;
    }
    
    public static class VSMessage {
        
        private static class HeaderHelper{
            
            public static final int MAX_MSG_LENGTH = (int) Byte.MAX_VALUE;

            public static byte createHeader(boolean hasNextMsg, int msgLength) throws Exception{
                boolean msgLengthIsValid = msgLength <= MAX_MSG_LENGTH || msgLength >= 0;
                if(!msgLengthIsValid){
                    throw new Exception("Invalid length");
                }
                byte header = (byte) (hasNextMsg ? -1 * msgLength : msgLength);
                return header;
            }
            
            public static int getLengthFromHeader(byte header){
                return Math.abs(header);
            }
            
            public static boolean headerIndicatesNextMessage(byte header){
                return header < 0;
            }
        }
        
        List<Byte> headers = new LinkedList<Byte>();
        List<byte[]> messages = new LinkedList<byte[]>();
        
        public VSMessage (byte[] plainMessage) throws Exception{
            
            int index = 0;
            boolean finishedParsing = false;
            while(!finishedParsing){

                int cpyFrom = index;
                int cpyTo = cpyFrom + HeaderHelper.MAX_MSG_LENGTH < plainMessage.length 
                        ? cpyFrom + HeaderHelper.MAX_MSG_LENGTH
                        : plainMessage.length;
                
                int bufferLength = cpyTo - cpyFrom;
                byte[] buffer = new byte[bufferLength];
                System.arraycopy(plainMessage, cpyFrom, buffer, 0, bufferLength);
                
                index = cpyTo;
                
                boolean hasNextMessage = index < plainMessage.length;
                
                byte header = HeaderHelper.createHeader(hasNextMessage, bufferLength);
                
                
                headers.add(header);
                messages.add(buffer);
                
                index = cpyTo;

                finishedParsing = !hasNextMessage;
            }
        }
        
        public VSMessage (InputStream stream) throws Exception{
            readMessageFromStream(stream);
        }
        
        private void readMessageFromStream(InputStream stream) throws Exception {
            
            boolean keepOnReading = true;
            while(keepOnReading){
                byte header = readHeaderFromStream(stream);
                int messageLength = HeaderHelper.getLengthFromHeader(header);
                
                byte[] messagePart = readDataFromStream(stream, messageLength);
                
                headers.add(header);
                messages.add(messagePart);
                
                keepOnReading = HeaderHelper.headerIndicatesNextMessage(header);
            }
        }
        
        private byte readHeaderFromStream(InputStream stream) throws Exception {
            int read = stream.read();
            if(read == -1){
                throw new Exception("Could not retrieve header from stream");
            }
            return (byte) read;
        }
        
        private byte[] readDataFromStream(InputStream stream, int count) throws IOException{
            
            byte[] buffer = new byte[count];

            int totalBytesRead = 0;
            while(totalBytesRead < count){
                int bytesRead = stream.read(buffer, totalBytesRead, count - totalBytesRead);
                totalBytesRead += bytesRead;
            }

            return buffer;
            
        }
        
        
        public byte[] getWrappedMessage(){
            
            byte[] fullWrappedMessage = new byte[getLengthOfFullWrappedMessage()];
            
            int index = 0;
            Iterator<Byte> headerIter = headers.iterator();
            Iterator<byte[]> msgIter = messages.iterator();
            
            while(headerIter.hasNext() && msgIter.hasNext()){
                byte header = (byte) headerIter.next();
                byte[] msg = (byte[]) msgIter.next();
                
                fullWrappedMessage[index++] = header;
                System.arraycopy(msg, 0, fullWrappedMessage, index, msg.length);
                index += msg.length;
            }
            
            return fullWrappedMessage;
        }
        
        private int getLengthOfFullWrappedMessage(){
            int lengthOfHeaders = headers.size();
            int lengthOfMessages = 0;
            for(byte header : headers){
                lengthOfMessages  += HeaderHelper.getLengthFromHeader(header);
            }
            return lengthOfHeaders + lengthOfMessages;
        }
        
        public byte[] getFullPlainMessage(){
            
            byte[] fullMsg = new byte[getLengthOfMessages()];
            
            int index = 0;
            for(byte[] msg : messages){
                System.arraycopy(msg, 0, fullMsg, index, msg.length);
                index += msg.length;
            }
            return fullMsg;
        }
        
        private int getLengthOfMessages(){
            int fullPlainMessageLength = 0;
            for(byte[] part : messages){
                fullPlainMessageLength += part.length;
            }
            return fullPlainMessageLength;
        }
        
        
    }
}
