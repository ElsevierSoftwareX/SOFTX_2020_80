/*
 * Copyright (c) 2004-2020 The YAWL Foundation. All rights reserved.
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

package org.yawlfoundation.yawl.worklet.support;

import org.yawlfoundation.yawl.engine.interfce.WorkItemRecord;
import org.yawlfoundation.yawl.worklet.WorkletService;

import java.util.HashSet;
import java.util.Set;

/**
 * On occasion, a workitem may be delegated to the worklet service for selection AND
 * have a workitem pre-constraint rule set that has to be evaluated by the exception
 * service (if exception handling is enabled). In those cases, selection has to be
 * postponed until exception evaluation and handling of the item has completed.
 *
 * This class queues selection events until the exception service notifies that it
 * is done with the item.
 *
 * @author Michael Adams
 * @date 23/02/2016
 */
public class EnabledEventQueue {

    private final Set<WorkItemRecord> _selectionQueue = new HashSet<WorkItemRecord>();
    private final Set<WorkItemRecord> _exceptionQueue = new HashSet<WorkItemRecord>();

    private static final Object MUTEX = new Object();


    public void notifySelectionEventReceived(WorkItemRecord wir) {
        processQueue(_exceptionQueue, _selectionQueue, wir);
    }


    public void notifyExceptionHandlingCompleted(WorkItemRecord wir) {
        processQueue(_selectionQueue, _exceptionQueue, wir);
    }


    // item cancelled
    public void removeItem(WorkItemRecord wir) {
        _selectionQueue.remove(wir);
        _exceptionQueue.remove(wir);
    }


    public void removeCase(String caseID) {
        removeCase(_selectionQueue, caseID);
        removeCase(_exceptionQueue, caseID);
    }


    private void removeCase(Set<WorkItemRecord> queue, String caseID) {
        Set<WorkItemRecord> toRemove = new HashSet<WorkItemRecord>();
        for (WorkItemRecord wir : queue) {
            if (wir.getRootCaseID().equals(caseID)) {
                toRemove.add(wir);
            }
        }
        queue.removeAll(toRemove);
    }


    public void processQueue(Set<WorkItemRecord> toProcess, Set<WorkItemRecord> toStore,
                             WorkItemRecord wir) {
        synchronized (MUTEX) {
            WorkItemRecord storedWIR = remove(toProcess, wir);
            if (storedWIR != null) {
                triggerSelectionEvent(storedWIR);
            }
            else {
                toStore.add(wir);
            }
        }
    }


    private void triggerSelectionEvent(WorkItemRecord wir) {
        WorkletService.getInstance().processEnabledWorkItemEvent(wir);
    }


    private WorkItemRecord remove(Set<WorkItemRecord> queue, WorkItemRecord toRemove) {
        for (WorkItemRecord wir : queue) {
            if (wir.getID().equals(toRemove.getID())) {
                queue.remove(wir);
                return wir;
            }
        }
        return null;
    }

}
