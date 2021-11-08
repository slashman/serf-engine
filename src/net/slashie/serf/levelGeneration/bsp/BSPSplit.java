package net.slashie.serf.levelGeneration.bsp;

import java.util.List;

public interface BSPSplit {
	public List<BSPRoom> splitRoom(BSPRoom room);

	public void setMinSplitSize(int minSplitSize);
}
