/*
 * IzPack - Copyright 2001-2008 Julien Ponge, All Rights Reserved.
 *
 * http://izpack.org/
 * http://izpack.codehaus.org/
 *
 * Copyright 2004 Klaus Bartz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.izforge.izpack.panels.userpath;

import com.izforge.izpack.api.adaptator.IXMLElement;
import com.izforge.izpack.api.data.Pack;
import com.izforge.izpack.api.data.ResourceManager;
import com.izforge.izpack.installer.base.InstallerFrame;
import com.izforge.izpack.installer.data.GUIInstallData;
import com.izforge.izpack.util.Debug;

/**
 * The target directory selection panel.
 *
 * @author Julien Ponge
 * @author Jeff Gordon
 */
public class UserPathPanel extends UserPathInputPanel
{

    private static final long serialVersionUID = 3256443616359429170L;

    private static String thisName = "UserPathPanel";

    private boolean skip = false;

    public static String pathVariableName = "UserPathPanelVariable";
    public static String pathPackDependsName = "UserPathPanelDependsName";
    public static String pathElementName = "UserPathPanelElement";

    /**
     * The constructor.
     *
     * @param parent The parent window.
     * @param idata  The installation installDataGUI.
     */
    public UserPathPanel(InstallerFrame parent, GUIInstallData idata, ResourceManager resourceManager)
    {
        super(parent, idata, thisName, resourceManager);
        // load the default directory info (if present)
        if (getDefaultDir() != null)
        {
            idata.setVariable(pathVariableName, getDefaultDir());
        }
    }

    /**
     * Called when the panel becomes active.
     */
    public void panelActivate()
    {
        boolean found = false;
        Debug.trace(thisName + " looking for activation condition");
        // Need to have a way to supress panel if not in selected packs.
        String dependsName = installData.getVariable(pathPackDependsName);
        if (dependsName != null && !(dependsName.equalsIgnoreCase("")))
        {
            Debug.trace("Checking for pack dependency of " + dependsName);
            for (Pack pack : installData.getSelectedPacks())
            {
                Debug.trace("- Checking if " + pack.name + " equals " + dependsName);
                if (pack.name.equalsIgnoreCase(dependsName))
                {
                    found = true;
                    Debug.trace("-- Found " + dependsName + ", panel will be shown");
                    break;
                }
            }
            skip = !(found);
        }
        else
        {
            Debug.trace("Not Checking for a pack dependency, panel will be shown");
            skip = false;
        }
        if (skip)
        {
            Debug.trace(thisName + " will not be shown");
            parent.skipPanel();
            return;
        }
        super.panelActivate();
        // Set the default or old value to the path selection panel.
        String expandedPath = installData.getVariable(pathVariableName);
        try
        {
            expandedPath = variableSubstitutor.substitute(expandedPath);
        }
        catch (Exception e)
        {
            // ignore
        }
        _pathSelectionPanel.setPath(expandedPath);
    }

    /**
     * Indicates whether the panel has been validated or not.
     *
     * @return Whether the panel has been validated or not.
     */
    public boolean isValidated()
    {
        // Standard behavior of PathInputPanel.
        if (!super.isValidated())
        {
            return (false);
        }
        installData.setVariable(pathVariableName, _pathSelectionPanel.getPath());
        return (true);
    }

    /**
     * Asks to make the XML panel installDataGUI.
     *
     * @param panelRoot The tree to put the installDataGUI in.
     */
    public void makeXMLData(IXMLElement panelRoot)
    {
        if (!(skip))
        {
            new UserPathPanelAutomationHelper(variableSubstitutor).makeXMLData(installData, panelRoot);
        }
    }

    /*
    * (non-Javadoc)
    *
    * @see com.izforge.izpack.installer.IzPanel#getSummaryBody()
    */

    public String getSummaryBody()
    {
        if (skip)
        {
            return null;
        }
        else
        {
            return (installData.getVariable(pathVariableName));
        }
    }
}
