package brymlee.pencil.internals;

import brymlee.pencil.Pencil;
import java.util.List;
import java.util.function.Function;

import static java.util.Arrays.*;
import static java.lang.Integer.*;

public enum PencilMethod {
    EDIT("edit", pencil -> arguments -> pencil.edit(parseInt(arguments.get(0)), arguments.get(1))),
    ERASE("erase", pencil -> arguments -> pencil.erase(arguments.get(0))),
    NEW_PENCIL("newPencil", pencil -> arguments -> pencil.newPencil(parseInt(arguments.get(0)),
                                                                    parseInt(arguments.get(1)),
                                                                    parseInt(arguments.get(2)),
                                                                    parseInt(arguments.get(3)))),
    SHARPEN("sharpen", pencil -> arguments -> pencil.sharpen()),
    WRITE("write", pencil -> arguments -> pencil.write(arguments));

    PencilMethod(final String description,
                 final Function<Pencil, Function<List<String>, Pencil>> method){
        this.description = description;
        this.method = method;
    }

    private String description;
    private Function<Pencil, Function<List<String>, Pencil>> method;

    public static PencilMethod fromDescription(final String pencilMethodDescription) {
        return asList(values())
            .stream()
            .filter(pencilMethod -> pencilMethodDescription.equals(pencilMethod.description()))
            .findFirst()
            .get();
    }

    public String description() {
        return this.description;
    }

    public Function<Pencil, Function<List<String>, Pencil>> method() {
        return this.method;
    }
}
