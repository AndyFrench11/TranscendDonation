package seng302.GUI.Controllers;

import seng302.GUI.StatusIndicator;
import seng302.GUI.TitleBar;

public abstract class PageController {

    protected StatusIndicator statusIndicator;
    protected TitleBar titleBar;
    protected UserWindowController parent;

    /**
     * Set the status indicator object from the user window the page is being displayed in
     *
     * @param statusIndicator the statusIndicator object
     */
    public void setStatusIndicator(StatusIndicator statusIndicator) {
        this.statusIndicator = statusIndicator;
    }

    /**
     * Assign the title bar of the window
     *
     * @param titleBar The title bar of the pane in which this pane is located
     */
    public void setTitleBar(TitleBar titleBar) {
        this.titleBar = titleBar;
    }

    /**
     * Set the parent window of the page
     * @param parent the UserWindowController which spawned the page
     */
    public void setParent(UserWindowController parent){ this.parent = parent;}

}
