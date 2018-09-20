package brymlee.pencil.internals;

import brymlee.pencil.Pencil;
import java.util.List;

public interface PencilMethodComposite {
    PencilMethod pencilMethod();
    List<String> pencilMethodArguments();
    Pencil pencil();

    static PencilMethodComposite pencilMethodComposite(final PencilMethod pencilMethod,
                                                       final List<String> pencilMethodArguments,
                                                       final Pencil pencil){
        return new PencilMethodComposite() {
            @Override
            public PencilMethod pencilMethod() {
                return pencilMethod;
            }

            @Override
            public List<String> pencilMethodArguments() {
                return pencilMethodArguments;
            }

            @Override
            public Pencil pencil() {
                return pencil;
            }
        };
    }
}
