package scheduler.observables;

import com.sun.javafx.collections.ObservableListWrapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * A {@link javafx.collections.ObservableList} that supports can be bindable list mutation operations.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <E> The list element type.
 */
public class MutationBindableObservableList<E> extends ObservableListWrapper<E> {

    public static <E> MutationOperation<E> createAddOperation(Iterator<E> iterator) {
        Objects.requireNonNull(iterator);
        if (iterator.hasNext()) {
            E first = iterator.next();
            if (iterator.hasNext()) {
                ArrayList<E> items = new ArrayList<>();
                items.add(first);
                do {
                    items.add(iterator.next());
                } while (iterator.hasNext());
                return new MutationOperation<E>() {
                    private final Optional<Collection<? extends E>> addOpt = Optional.of(Collections.unmodifiableList(items));

                    @Override
                    public Optional<Collection<? extends E>> addAll() {
                        return addOpt;
                    }
                };
            }
            return new MutationOperation<E>() {
                private final Optional<E> addOpt = (null == first) ? Optional.ofNullable(first) : Optional.of(first);

                @Override
                public Optional<E> add() {
                    return addOpt;
                }
            };
        }
        return new MutationOperation<E>() {

            private final Optional<Collection<? extends E>> addOpt = Optional.of(Collections.emptyList());

            @Override
            public Optional<Collection<? extends E>> addAll() {
                return addOpt;
            }
        };
    }

    @SuppressWarnings("unchecked")
    public static <E> MutationOperation<E> createAddOperation(Collection<E> coll) {
        Objects.requireNonNull(coll);
        if (coll.size() == 1) {
            E v = coll.iterator().next();
            return new MutationOperation<E>() {
                private final Optional<E> addOpt = (null == v) ? Optional.ofNullable(v) : Optional.of(v);

                @Override
                public Optional<E> add() {
                    return addOpt;
                }
            };
        }
        return new MutationOperation<E>() {

            private final Optional<Collection<? extends E>> addOpt = Optional.of(Collections.unmodifiableList((coll instanceof List) ? (List<E>) coll : new ArrayList(coll)));

            @Override
            public Optional<Collection<? extends E>> addAll() {
                return addOpt;
            }
        };
    }

    @SuppressWarnings("unchecked")
    public static <E> MutationOperation<E> createAddOperation(E... value) {
        return createAddOperation(Arrays.asList(Objects.requireNonNull(value)));
    }

    public static <T> ObjectBinding<MutationOperation<T>> createAddOperationBinding(Collection<T> value) {
        MutationOperation<T> operation = createAddOperation(Objects.requireNonNull(value));
        return Bindings.createObjectBinding(() -> operation);
    }

    @SuppressWarnings("unchecked")
    public static <T> ObjectBinding<MutationOperation<T>> createAddOperationBinding(T... value) {
        MutationOperation<T> operation = createAddOperation(Objects.requireNonNull(value));
        return Bindings.createObjectBinding(() -> operation);
    }

    public static <E> MutationOperation<E> createRemoveOperation(Iterator<E> iterator) {
        Objects.requireNonNull(iterator);
        if (iterator.hasNext()) {
            E first = iterator.next();
            if (iterator.hasNext()) {
                ArrayList<E> items = new ArrayList<>();
                items.add(first);
                do {
                    items.add(iterator.next());
                } while (iterator.hasNext());
                return new MutationOperation<E>() {
                    private final Optional<Collection<? extends E>> removeOpt = Optional.of(Collections.unmodifiableList(items));

                    @Override
                    public Optional<Collection<? extends E>> removeAll() {
                        return removeOpt;
                    }
                };
            }
            return new MutationOperation<E>() {
                private final Optional<E> removeOpt = (null == first) ? Optional.ofNullable(first) : Optional.of(first);

                @Override
                public Optional<E> remove() {
                    return removeOpt;
                }
            };
        }
        return new MutationOperation<E>() {

            private final Optional<Collection<? extends E>> removeOpt = Optional.of(Collections.emptyList());

            @Override
            public Optional<Collection<? extends E>> removeAll() {
                return removeOpt;
            }
        };
    }

    @SuppressWarnings("unchecked")
    public static <E> MutationOperation<E> createRemoveOperation(Collection<E> coll) {
        Objects.requireNonNull(coll);
        if (coll.size() == 1) {
            E v = coll.iterator().next();
            return new MutationOperation<E>() {
                private final Optional<E> removeOpt = (null == v) ? Optional.ofNullable(v) : Optional.of(v);

                @Override
                public Optional<E> remove() {
                    return removeOpt;
                }
            };
        }
        return new MutationOperation<E>() {

            private final Optional<Collection<? extends E>> removeOpt = Optional.of(Collections.unmodifiableList((coll instanceof List) ? (List<E>) coll : new ArrayList(coll)));

            @Override
            public Optional<Collection<? extends E>> removeAll() {
                return removeOpt;
            }
        };
    }

    @SuppressWarnings("unchecked")
    public static <E> MutationOperation<E> createRemoveOperation(E... value) {
        return createRemoveOperation(Arrays.asList(Objects.requireNonNull(value)));
    }

    public static <E> ObjectBinding<MutationOperation<E>> createRemoveOperationBinding(Collection<E> coll) {
        MutationOperation<E> operation = createRemoveOperation(Objects.requireNonNull(coll));
        return Bindings.createObjectBinding(() -> operation);
    }

    @SuppressWarnings("unchecked")
    public static <E> ObjectBinding<MutationOperation<E>> createRemoveOperationBinding(E... value) {
        MutationOperation<E> operation = createRemoveOperation(Objects.requireNonNull(value));
        return Bindings.createObjectBinding(() -> operation);
    }

    @SuppressWarnings("unchecked")
    public static <E> MutationOperation<E> createRemoveAddOperation(Collection<E> toRemove, Collection<E> toAdd) {
        ArrayList<E> rc = new ArrayList();
        rc.addAll(Objects.requireNonNull(toRemove));
        ArrayList<E> ac = new ArrayList();
        ac.addAll(Objects.requireNonNull(toAdd));
        if (rc.isEmpty()) {
            if (ac.size() == 1) {
                return new MutationOperation<E>() {
                    private final Optional<E> addOpt = (null == ac.get(0)) ? Optional.ofNullable(ac.get(0)) : Optional.of(ac.get(0));

                    @Override
                    public Optional<E> add() {
                        return addOpt;
                    }
                };
            }
            return new MutationOperation<E>() {
                private final Optional<Collection<? extends E>> addOpt = Optional.of(Collections.unmodifiableList(ac));

                @Override
                public Optional<Collection<? extends E>> addAll() {
                    return addOpt;
                }
            };
        }
        if (ac.isEmpty()) {
            if (rc.size() == 1) {
                return new MutationOperation<E>() {
                    private final Optional<E> removeOpt = (null == rc.get(0)) ? Optional.ofNullable(rc.get(0)) : Optional.of(rc.get(0));

                    @Override
                    public Optional<E> remove() {
                        return removeOpt;
                    }
                };
            }
            return new MutationOperation<E>() {
                private final Optional<Collection<? extends E>> removeOpt = Optional.of(Collections.unmodifiableList(rc));

                @Override
                public Optional<Collection<? extends E>> removeAll() {
                    return removeOpt;
                }
            };
        }
        if (rc.size() == 1) {
            final Optional<E> removeOpt = (null == rc.get(0)) ? Optional.ofNullable(rc.get(0)) : Optional.of(rc.get(0));
            if (ac.size() == 1) {
                return new MutationOperation<E>() {
                    private final Optional<E> addOpt = (null == ac.get(0)) ? Optional.ofNullable(ac.get(0)) : Optional.of(ac.get(0));

                    @Override
                    public Optional<E> add() {
                        return addOpt;
                    }

                    @Override
                    public Optional<E> remove() {
                        return removeOpt;
                    }
                };
            }
            return new MutationOperation<E>() {
                private final Optional<Collection<? extends E>> addOpt = Optional.of(Collections.unmodifiableList(ac));

                @Override
                public Optional<Collection<? extends E>> addAll() {
                    return addOpt;
                }

                @Override
                public Optional<E> remove() {
                    return removeOpt;
                }
            };
        }
        final Optional<Collection<? extends E>> removeAllOpt = Optional.of(Collections.unmodifiableList(rc));
        if (ac.size() == 1) {
            return new MutationOperation<E>() {
                private final Optional<E> addOpt = (null == ac.get(0)) ? Optional.ofNullable(ac.get(0)) : Optional.of(ac.get(0));

                @Override
                public Optional<E> add() {
                    return addOpt;
                }

                @Override
                public Optional<Collection<? extends E>> removeAll() {
                    return removeAllOpt;
                }
            };
        }
        return new MutationOperation<E>() {
            private final Optional<Collection<? extends E>> addOpt = Optional.of(Collections.unmodifiableList(ac));

            @Override
            public Optional<Collection<? extends E>> addAll() {
                return addOpt;
            }

            @Override
            public Optional<Collection<? extends E>> removeAll() {
                return removeAllOpt;
            }
        };
    }

    @SuppressWarnings("unchecked")
    public static <T> MutationOperation<T> createRemoveAddOperation(Collection<T> toRemove, T... toAdd) {
        return createRemoveAddOperation(toRemove, Arrays.asList(Objects.requireNonNull(toAdd)));
    }

    public static <T> MutationOperation<T> createRemoveAddOperation(T[] toRemove, Collection<T> toAdd) {
        return createRemoveAddOperation(Arrays.asList(Objects.requireNonNull(toRemove)), toAdd);
    }

    @SuppressWarnings("unchecked")
    public static <T> MutationOperation<T> createRemoveAddOperation(T[] toRemove, T... toAdd) {
        return createRemoveAddOperation(Arrays.asList(Objects.requireNonNull(toRemove)), Arrays.asList(Objects.requireNonNull(toAdd)));
    }

    public static <T> ObjectBinding<MutationOperation<T>> createRemoveAddOperationBinding(Collection<T> toRemove, Collection<T> toAdd) {
        MutationOperation<T> operation = createRemoveAddOperation(toRemove, toAdd);
        return Bindings.createObjectBinding(() -> operation);
    }

    @SuppressWarnings("unchecked")
    public static <T> ObjectBinding<MutationOperation<T>> createRemoveAddOperationBinding(Collection<T> toRemove, T... toAdd) {
        MutationOperation<T> operation = createRemoveAddOperation(toRemove, toAdd);
        return Bindings.createObjectBinding(() -> operation);
    }

    public static <T> ObjectBinding<MutationOperation<T>> createRemoveAddOperationBinding(T[] toRemove, Collection<T> toAdd) {
        MutationOperation<T> operation = createRemoveAddOperation(toRemove, toAdd);
        return Bindings.createObjectBinding(() -> operation);
    }

    @SuppressWarnings("unchecked")
    public static <T> ObjectBinding<MutationOperation<T>> createRemoveAddOperationBinding(T[] toRemove, T... toAdd) {
        MutationOperation<T> operation = createRemoveAddOperation(toRemove, toAdd);
        return Bindings.createObjectBinding(() -> operation);
    }

    private final MutationProperty mutation;
    private final MutationProperty.SetProperty mutationSet;

    /**
     * Creates a new {@code AlternatingObservableList} that is backed by the specified list.
     *
     * @param list a concrete List that backs this {@code AlternatingObservableList}.
     */
    public MutationBindableObservableList(List<E> list) {
        super(list);
        this.mutation = new MutationProperty();
        this.mutationSet = mutation.setProperty;
    }

    /**
     * Creates an empty {@code AlternatingObservableList} that is backed an {@link ArrayList}.
     */
    public MutationBindableObservableList() {
        this(new ArrayList<>());
    }

    /**
     * Gets the last mutation operation that was performed on the current {@code MutationBindableObservableList}.
     *
     * @return The last mutation operation that was performed on the current {@code MutationBindableObservableList}.
     */
    public MutationOperation getMutation() {
        return mutation.get();
    }

    /**
     * Applies a new {@link MutationOperation} to the current {@code MutationBindableObservableList}.
     * <p>
     * The modifications specified in the {@link MutationOperation} will be immediately applied to the current {@link MutationBindableObservableList}
     * within a single change block.</p>
     * <p>
     * Whenever this accessor is called, the change operation will occur, even if the object being applied is the same object as the current value.
     * Also, {@link #mutationSet} property will be updated with a singleton {@link Set} containing the current {@link MutationOperation}.</p>
     * <p>
     * Null values will be applied to the property, but will result in no actual changes.</p>
     *
     * @param operation The mutation operation to be performed.
     */
    public void setMutation(MutationOperation operation) {
        mutation.set(operation);
    }

    /**
     * Applies a new {@link MutationOperation} to the current {@code MutationBindableObservableList} if property value changes.
     * <p>
     * The modifications specified in the {@link MutationOperation} will be immediately applied to the current {@link MutationBindableObservableList}
     * within a single change block.</p>
     * <p>
     * The change operation will only actually occur if the object being applied is not the same object as current value.</p>
     * <p>
     * As a mutation is applied, the {@link #mutationSet} property will be updated with a singleton {@link Set} containing the current
     * {@link MutationOperation}.</p>
     * <p>
     * Null values will be applied to the property, but will result in no actual changes.</p>
     *
     * @param operation The mutation operation to be performed.
     */
    public synchronized void setMutationIfNew(MutationOperation operation) {
        if (!Objects.equals(mutation.get(), operation)) {
            mutation.set(operation);
        }
    }

    /**
     * Gets the {@link ObjectProperty} that can be bound to {@link MutationOperation}s.
     * <p>
     * The modifications specified in the bound {@link MutationOperation} will be immediately applied to the current
     * {@link MutationBindableObservableList} within a single change block.</p>
     * <p>
     * Whenever this the binding is invalidated, the change operation will occur, even if the bound object is the same object as the current value.
     * Also, the {@link #mutationSet} property will be updated with a singleton {@link Set} containing the current {@link MutationOperation}.</p>
     * <p>
     * Null values will be applied to the property, but will result in no actual changes.</p>
     *
     * @return An {@link ObjectProperty} that can be bound to {@link MutationOperation}s.
     */
    public ObjectProperty<MutationOperation> mutationProperty() {
        return mutation;
    }

    /**
     * Gets the last set of mutation operations that were performed on the current {@code MutationBindableObservableList}.
     *
     * @return The last set of mutation operations that were performed on the current {@code MutationBindableObservableList}.
     */
    public Set<MutationOperation> getMutationSet() {
        return mutationSet.get();
    }

    /**
     * Applies a new set of {@link MutationOperation}s to the current {@code MutationBindableObservableList}.
     * <p>
     * The modifications specified in the {@link MutationOperation}s will be immediately applied to the current {@link MutationBindableObservableList}
     * within a single change block.</p>
     * <p>
     * Whenever this accessor is called, the change operations will occur, even if the objects being applied are the same as the current set. As each
     * {@link MutationOperation} is performed the {@link #mutation} property will be updated with the latest individual {@link MutationOperation}.</p>
     * <p>
     * Null values will be applied to the property, but will result in no actual changes.</p>
     *
     * @param operations The mutation operations to be performed.
     */
    public void setMutationSet(Set<MutationOperation> operations) {
        mutationSet.set(operations);
    }

    /**
     * Gets the {@link ObjectProperty} that can be bound to sets of {@link MutationOperation}s.
     * <p>
     * The modifications specified in the bound {@link Set} of {@link MutationOperation}s will be immediately applied to the current
     * {@link MutationBindableObservableList} within a single change block.</p>
     * <p>
     * Whenever this the binding is invalidated, the change operations will occur, even if the objects being applied are the same as the current set.
     * As each {@link MutationOperation} is performed the {@link #mutation} property will be updated with the latest individual
     * {@link MutationOperation}.</p>
     * <p>
     * Null values will be applied to the property, but will result in no actual changes.</p>
     *
     * @return An {@link ObjectProperty} that can be bound to {@link Set}s of {@link MutationOperation}s.
     */
    public ObjectProperty<Set<MutationOperation>> mutationSetProperty() {
        return mutationSet;
    }

    /**
     * Defines mutation operation to be performed on the {@link MutationBindableObservableList}.
     * <p>
     * This represents a modification to a {@link MutationBindableObservableList} that will occur in a single change block.</p>
     * <p>
     * Following is the precedence list for the mutations supported by this interface: {@link MutationOperation#clear()},
     * {@link MutationOperation#removeAll()}, {@link MutationOperation#retainAll()}, {@link MutationOperation#removeIf()},
     * {@link MutationOperation#remove()}, {@link MutationOperation#replaceAll()}, {@link MutationOperation#setAll()},
     * {@link MutationOperation#addAll()}, {@link MutationOperation#add()}, {@link MutationOperation#sort()}</p>
     *
     * @param <E> The element type.
     */
    @SuppressWarnings("unchecked")
    private synchronized void mutate(Set<MutationOperation> mutation, Consumer<MutationOperation> onMutate) {
        if (null == mutation || mutation.isEmpty()) {
            return;
        }
        beginChange();
        try {
            mutation.forEach((t) -> {
                onMutate.accept(t);
                if (t.clear()) {
                    super.clear();
                }
                Optional<Collection<? extends E>> c = t.removeAll();
                if (null != c && c.isPresent()) {
                    super.removeAll(c.get());
                }
                c = t.retainAll();
                if (null != c && c.isPresent()) {
                    super.retainAll(c.get());
                }
                Optional<Predicate<? super E>> f = t.removeIf();
                if (null != f && f.isPresent()) {
                    super.removeIf(f.get());
                }
                Optional<? extends E> e = t.remove();
                if (null != e && e.isPresent()) {
                    super.remove(e.get());
                }
                Optional<UnaryOperator<E>> u = t.replaceAll();
                if (null != u && u.isPresent()) {
                    super.replaceAll(u.get());
                }
                c = t.setAll();
                if (null != c && c.isPresent()) {
                    super.setAll(c.get());
                }
                c = t.addAll();
                if (null != c && c.isPresent()) {
                    super.addAll(c.get());
                }
                e = t.add();
                if (null != e && e.isPresent()) {
                    super.add(e.get());
                }
                Optional<Comparator<? super E>> s = t.sort();
                if (null != s && s.isPresent()) {
                    Comparator<? super E> comparator = s.get();
                    if (null == comparator) {
                        super.sort();
                    } else {
                        super.sort(comparator);
                    }
                }
            });
        } finally {
            endChange();
        }
    }

    private class MutationProperty extends SimpleObjectProperty<MutationOperation> {

        private final SetProperty setProperty = new SetProperty();

        @Override
        public void set(MutationOperation newValue) {
            super.set(newValue);
            MutationBindableObservableList.this.mutationSet.set(Collections.singleton(super.get()));
        }

        @Override
        public final void setValue(MutationOperation v) {
            this.set(v);
        }

        private void setInternal(MutationOperation newValue) {
            super.set(newValue);
        }

        class SetProperty extends SimpleObjectProperty<Set<MutationOperation>> {

            @Override
            public void set(Set<MutationOperation> newValue) {
                super.set(newValue);
                MutationBindableObservableList.this.mutate(super.get(), MutationBindableObservableList.this.mutation::setInternal);
            }

            @Override
            public final void setValue(Set<MutationOperation> v) {
                this.set(v);
            }

        }
    }

    /**
     * Defines mutation operation to be performed on the {@link MutationBindableObservableList}.
     * <p>
     * This represents a modification to a {@link MutationBindableObservableList} that will occur in a single change block.</p>
     * <p>
     * Following is the precedence list for the mutations supported by this interface: {@link MutationOperation#clear()},
     * {@link MutationOperation#removeAll()}, {@link MutationOperation#retainAll()}, {@link MutationOperation#removeIf()},
     * {@link MutationOperation#remove()}, {@link MutationOperation#replaceAll()}, {@link MutationOperation#setAll()},
     * {@link MutationOperation#addAll()}, {@link MutationOperation#add()}, {@link MutationOperation#sort()}</p>
     *
     * @param <E> The element type.
     */
    public interface MutationOperation<E> {

        default boolean clear() {
            return false;
        }

        default Optional<Collection<? extends E>> removeAll() {
            return Optional.empty();
        }

        default Optional<Collection<? extends E>> retainAll() {
            return Optional.empty();
        }

        default Optional<Predicate<? super E>> removeIf() {
            return Optional.empty();
        }

        default Optional<? extends E> remove() {
            return Optional.empty();
        }

        default Optional<UnaryOperator<E>> replaceAll() {
            return Optional.empty();
        }

        default Optional<Collection<? extends E>> setAll() {
            return Optional.empty();
        }

        default Optional<Collection<? extends E>> addAll() {
            return Optional.empty();
        }

        default Optional<? extends E> add() {
            return Optional.empty();
        }

        default Optional<Comparator<? super E>> sort() {
            return Optional.empty();
        }
    }

}
