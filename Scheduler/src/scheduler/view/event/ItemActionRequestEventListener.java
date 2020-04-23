package scheduler.view.event;

/**
 * Listens for item edit or delete request events.
 * <p>The {@link scheduler.controls.ItemEditTableCellFactory} fires the {@link ItemActionRequestEvent} when the edit or delete
 * button is clicked on a {@link scheduler.controls.ItemEditTableCell}.</p>
 *
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
 * @param <T> The target item type. 
 */
public interface ItemActionRequestEventListener<T> {
    
    /**
     * Handles a {@link ItemActionRequestEvent}.
     * 
     * @param event The {@link ItemActionRequestEvent} that occurred.
     */
    void acceptItemActionRequest(ItemActionRequestEvent<T> event);

}
