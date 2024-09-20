package org.rb98dps.splitwise.handlers.expense;

import org.rb98dps.splitwise.dto.request.ExpenseRequest;
import org.rb98dps.splitwise.managers.GroupManager;


public abstract class ExpenseHandler {

    public GroupManager groupManager = GroupManager.getInstance();
    ExpenseHandler nextHandler;

    public ExpenseHandler(ExpenseHandler handler) {
        nextHandler = handler;
    }

    /**
     * chain of responsibility for handling expense, default method
     *
     * @param request expense request to be processed
     */
    public void handle(ExpenseRequest request) {
        if (nextHandler != null) {
            nextHandler.handle(request);
        }
    }
}
