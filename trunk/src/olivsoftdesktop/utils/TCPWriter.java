package olivsoftdesktop.utils;

import coreutilities.log.Logger;

import java.io.DataOutputStream;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import olivsoftdesktop.ctx.DesktopContext;

public class TCPWriter
{
  private List<Socket> clientSocketlist = new ArrayList<Socket>(1);
  
  private int tcpPort               = 7001;
  private ServerSocket serverSocket = null;
  
  public TCPWriter(int port) throws Exception
  {
    this.tcpPort = port;

    try 
    { 
      SocketThread socketThread = new SocketThread(this);
      socketThread.start();      
    }
    catch (Exception ex)
    {
      try { JOptionPane.showMessageDialog(null, ex.getLocalizedMessage(), "TCP Writer", JOptionPane.ERROR_MESSAGE); } catch (Exception ex2) {}
      Logger.log(ex.getLocalizedMessage(), Logger.INFO);      
      throw ex;
    }
  }
  
  protected void setSocket(Socket skt)
  {
    this.clientSocketlist.add(skt);
    if (DesktopContext.getInstance().isDesktopVerbose())
      System.out.println("- " + clientSocketlist.size() + " TCP Client socket(s)");
  }
  
  public void write(byte[] message)
  {
    if (DesktopContext.getInstance().isDesktopVerbose())
    {
      System.out.println("TCP write on port " + tcpPort + " [" + new String(message, 0, message.length - 2) + "]");
      System.out.print("[");
      for (byte b : message)
        System.out.print(formatByteHexa(b) + " ");
      System.out.println("]");
    }
    synchronized (clientSocketlist)
    {
      List<Socket> toRemove = new ArrayList<Socket>();
      for (Socket tcpSocket : clientSocketlist)
      {
        try
        {
          DataOutputStream out = null;  
          if (out == null)
            out = new DataOutputStream(tcpSocket.getOutputStream());
          out.write(message);
          out.flush();
        }
        catch (SocketException se)
        {
          toRemove.add(tcpSocket);
        }
        catch (Exception ex)
        {
          System.err.println("TCPWriter.write:" + ex.getLocalizedMessage());
          ex.printStackTrace();
        }
      }
      if (toRemove.size() > 0)
      {
        for (Socket skt : toRemove)
        {
          this.clientSocketlist.remove(skt);
          if (DesktopContext.getInstance().isDesktopVerbose())
            System.out.println("- " + clientSocketlist.size() + " TCP Client socket(s)");
        }
      }
    }
  }
  
  private String formatByteHexa(byte b)
  {
    String s = Integer.toHexString(b).toUpperCase();
    while (s.length() < 2)
      s = "0" + s;
    return s;
  }
  
  public void close() throws Exception
  {
    for (Socket tcpSocket : clientSocketlist)
      tcpSocket.close();  
  }
  
  public static void main_(String[] args)
  {
    String gpsd = "{\"class\":\"TVP\",\"tag\":\"MID2\",\"time\":\"2010-04-30T11:48:20.10Z\",\"ept\":0.005,\"lat\":46.498204497,\"lon\":7.568061439,\"alt\":1327.689,\"epx\":15.319,\"epy\":17.054,\"epv\":124.484,\"track\":10.3797,\"speed\":0.091,\"climb\":-0.085,\"eps\",34.11,\"mode\":3}";
    String wpl  = "$GPWPL,3739.856,N,12222.812,W,OPMRNA*59";
    try
    {
      TCPWriter tcpw = new TCPWriter(2947);
//    TCPWriter tcpw = new TCPWriter(7001);
//    TCPWriter tcpw = new TCPWriter(7001, "theketch-lap.mshome.net");
      for (int i=0; i<50; i++)
      {
        System.out.println("Ping...");
        try { tcpw.write(gpsd.getBytes()); } catch (Exception ex) { System.err.println(ex.getLocalizedMessage()); }
        try { Thread.sleep(1000L); } catch (Exception ex) { ex.printStackTrace(); }
      }
    }
    catch (Exception e)
    {
      // TODO: Add catch code
      e.printStackTrace();
    }
  }
  
  private class SocketThread extends Thread
  {
    private TCPWriter parent = null;
    public SocketThread(TCPWriter parent)
    {
      super();
      this.parent = parent;
    }
    public void run()
    {
      try 
      { 
        parent.serverSocket = new ServerSocket(tcpPort);
        while (true) // Wait for the clients
        {
//        System.out.println(".......... serverSocket waiting (TCP:" + tcpPort + ").");
          Socket clientSkt = serverSocket.accept(); 
//        System.out.println(".......... serverSocket accepted (TCP:" + tcpPort + ").");
          parent.setSocket(clientSkt);
        }
      } 
      catch (Exception ex) 
      { System.err.println("SocketThread:" + ex.getLocalizedMessage()); } 
      System.out.println("..... End of TCP SocketThread.");
    }
  }
}
