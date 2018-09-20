package brymlee.pencil;

import brymlee.pencil.internals.PencilMethod;
import brymlee.pencil.internals.PencilMethodComposite;
import java.util.List;
import java.util.stream.Stream;

import static brymlee.pencil.internals.PencilMethod.fromDescription;
import static brymlee.pencil.internals.PencilMethodComposite.pencilMethodComposite;
import static java.util.Arrays.*;
import static java.util.stream.IntStream.*;
import static java.util.stream.Collectors.*;
import static brymlee.pencil.Pencil.*;

public class PencilCliApplication {

    public static void main(final String[] arguments){
        runWithMain(asList(arguments));
    }

    public static Pencil runWithMain(final List<String> arguments){
        final List<String> methodArgumentSplit = asList(arguments.stream().reduce((i, j) -> i.concat("\t").concat(j)).get().split("and"));
        final Stream<PencilMethodComposite> incompleteCompositeStream = methodArgumentSplit
            .stream()
            .map(methodArguments -> asList(methodArguments.split("\t")))
            .map(methodArguments -> methodArguments.stream().filter(methodArgument -> !"".equals(methodArgument.trim())).collect(toList()))
            .map(methodArguments -> pencilMethodComposite(fromDescription(methodArguments.get(0)),
                                                          range(1, methodArguments.size())
                                                                .mapToObj(index -> methodArguments.get(index))
                                                                .collect(toList()),
                                                          null));
        final Pencil defaultPencil = newPencilStatic(() -> "", 0, 0, 0, 0);
        final PencilMethodComposite defaultPencilMethodComposite = pencilMethodComposite(null, null, defaultPencil);
        final Stream<PencilMethodComposite> completeCompositeStream = Stream.concat(asList(defaultPencilMethodComposite).stream(), incompleteCompositeStream);
        final Pencil newPencil = completeCompositeStream
            .reduce((i, j) -> {
                final Pencil pencil = j.pencilMethod().method().apply(i.pencil()).apply(j.pencilMethodArguments());
                return pencilMethodComposite(i.pencilMethod(), i.pencilMethodArguments(), pencil);
            }).get()
            .pencil();
        System.out.println("Output: ".concat(newPencil.paper().text()));
        return newPencil;
    }
}
