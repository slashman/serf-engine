package net.slashie.serf.ui;
import java.util.List;
import org.apache.log4j.Logger;
import net.slashie.serf.action.Action;
import net.slashie.serf.action.Actor;
import net.slashie.serf.action.Message;
public class DebugInterface extends UserInterface
{
	final static Logger logger = Logger.getRootLogger();

	public DebugInterface()
	{
		// TODO Auto-generated constructor stub
	}

	@Override
	public void doLook()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public boolean promptChat(String message)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void drawEffect(Effect what)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void addMessage(Message message)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public List<Message> getMessageBuffer()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isDisplaying(Actor who)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setTargets(Action a) throws ActionCancelException
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void showMessageHistory()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void showInventory()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public int switchChat(String title, String prompt, String... options)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String inputBox(String prompt)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean prompt()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void refresh()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void showMessage(String x)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void showImportantMessage(String x)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void showSystemMessage(String x)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void processQuit()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void processSave()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void processHelp()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onMusicOn()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void reset()
	{
		// TODO Auto-generated method stub

	}
}

