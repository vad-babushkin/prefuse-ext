package prefuse.demos.projektlp;

public class JumpBackProcess implements Runnable
{
	
	public JumpBackProcess(MainDisplay md, Buttons b) 
	{
		
	}

	private void jumpBack()
	{
		long fp = 0;
		
		try
		{
			fp = ProcessData.raf.getFilePointer();
			System.out.println(fp);
			ProcessData.raf.seek(fp - 2*ProcessData.BUFF_SIZE);
			ProcessData.raf.seek(fp - 2*ProcessData.BUFF_SIZE);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void run()
	{
		jumpBack();
	}
}
