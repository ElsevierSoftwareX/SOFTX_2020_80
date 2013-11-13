/*
 * Copyright (c) 2004-2013 The YAWL Foundation. All rights reserved.
 * The YAWL Foundation is a collaboration of individuals and
 * organisations who are committed to improving workflow technology.
 *
 * This file is part of YAWL. YAWL is free software: you can
 * redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation.
 *
 * YAWL is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General
 * Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with YAWL. If not, see <http://www.gnu.org/licenses/>.
 */

package org.yawlfoundation.yawl.editor.core.repository;

import org.yawlfoundation.yawl.elements.YAWLServiceGateway;
import org.yawlfoundation.yawl.elements.YDecomposition;
import org.yawlfoundation.yawl.exceptions.YSyntaxException;
import org.yawlfoundation.yawl.util.XNode;

import java.util.List;

/**
 * @author Michael Adams
 * @date 6/08/13
 */
public class TaskDecompositionRepository extends DecompositionRepoMap {

    protected TaskDecompositionRepository() {
        super("taskDecompositions");
    }


    /**
     * Adds a task decomposition to the repository
     * @param name a reference name for the task decomposition
     * @param description a description of it
     * @param gateway the task decomposition to add
     * @return whether the add was successful
     */
    public String add(String name, String description, YAWLServiceGateway gateway) {
        return super.add(name, description, gateway);
    }


    /**
     * Gets a task decomposition from the repository
     * @param name a reference name for the task decomposition
     * @return the referenced task decomposition, or null if not found
     */
    public YAWLServiceGateway get(String name) throws YSyntaxException {
        return (YAWLServiceGateway) super.get(name);
    }


    /**
     * Removes a task decomposition from the repository
     * @param name a reference name for the task decomposition
     * @return whether the removal was successful
     */
    public YAWLServiceGateway remove(String name) {
        return (YAWLServiceGateway) super.remove(name);
    }


    /**
     * Gets a sorted list of descriptors for all stored task decompositions
     * @return A sorted list of RepoDescriptors (String pairs - name, description)
     */
    public List<RepoDescriptor> getDescriptors() {
        return super.getDescriptors();
    }


    // writes a task decomposition to xml
    // since a specification adds some xml to that generated by a decomposition, we
    // have to replicate it here
    protected String toXML(YDecomposition decomposition) {
        YAWLServiceGateway serviceGateway = (YAWLServiceGateway) decomposition;
        XNode decompNode = new XNode("decomposition");
        decompNode.addAttribute("id", serviceGateway.getID());
        decompNode.addAttributes(serviceGateway.getAttributes());     // extended attr.
        decompNode.addContent(serviceGateway.toXML());
        String interaction = serviceGateway.requiresResourcingDecisions() ?
                "manual": "automated";
        decompNode.addChild("externalInteraction", interaction);
        String codelet = serviceGateway.getCodelet();
        if (codelet != null) {
            decompNode.addChild("codelet", codelet);
        }
        return decompNode.toString();
    }


    protected void addXsiAttribute(XNode decompositionNode) {
        decompositionNode.addAttribute("xsi:type", "WebServiceGatewayFactsType");
    }


}