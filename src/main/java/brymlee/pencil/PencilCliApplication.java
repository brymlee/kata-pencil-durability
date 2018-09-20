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
    private static final Pencil DEFAULT_PENCIL = newPencilStatic(() -> "", 0, 0, 0, 0);
    private static final PencilMethodComposite DEFAULT_PENCIL_METHOD_COMPOSITE = pencilMethodComposite(null, null, DEFAULT_PENCIL);

    public static void main(final String[] arguments){
        runWithMain(asList(arguments));
    }

    private static List<String> splitArgumentsOnTab(final String methodArguments){
        return asList(methodArguments.split("\t"));
    }

    private static List<String> filterOutEmptyArguments(final List<String> methodArguments){
        return methodArguments
            .stream()
            .filter(methodArgument -> !"".equals(methodArgument.trim()))
            .collect(toList());
    }

    private static PencilMethodComposite reduceToApplyPencilMethodsToResultPencil(final PencilMethodComposite i,
                                                                                  final PencilMethodComposite j){
        final Pencil pencil = j.pencilMethod().method().apply(i.pencil()).apply(j.pencilMethodArguments());
        return pencilMethodComposite(i.pencilMethod(), i.pencilMethodArguments(), pencil);
    }

    private static PencilMethodComposite methodArgumentsToPencilMethodComposite(final List<String> methodArguments){
        final PencilMethod pencilMethod = fromDescription(methodArguments.get(0));
        final List<String> methodArgumentsWithoutKey = range(1, methodArguments.size())
            .mapToObj(index -> methodArguments.get(index))
            .collect(toList());
        return pencilMethodComposite(pencilMethod, methodArgumentsWithoutKey,null);
    }

    private static List<String> methodArgumentSplit(final List<String> arguments){
        return asList(arguments.stream().reduce((i, j) -> i.concat("\t").concat(j)).get().split("and"));
    }

    public static Pencil runWithMain(final List<String> arguments){
        if(!PencilMethod.NEW_PENCIL.description().equals(arguments.get(0))){
            throw new RuntimeException("You must call newPencil when using the pencil off of the command line.");
        }
        final List<String> methodArgumentSplit = methodArgumentSplit(arguments);
        final Stream<PencilMethodComposite> incompleteCompositeStream = methodArgumentSplit
            .stream()
            .map(PencilCliApplication::splitArgumentsOnTab)
            .map(PencilCliApplication::filterOutEmptyArguments)
            .map(PencilCliApplication::methodArgumentsToPencilMethodComposite);
        final Stream<PencilMethodComposite> completeCompositeStream = Stream.concat(asList(DEFAULT_PENCIL_METHOD_COMPOSITE).stream(), incompleteCompositeStream);
        final Pencil newPencil = completeCompositeStream
            .reduce(PencilCliApplication::reduceToApplyPencilMethodsToResultPencil)
            .get()
            .pencil();
        System.out.println("Output: ".concat(newPencil.paper().text()));
        return newPencil;
    }
}
