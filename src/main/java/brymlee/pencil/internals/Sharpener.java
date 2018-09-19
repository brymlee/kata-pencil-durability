package brymlee.pencil.internals;

import brymlee.pencil.Pencil;

@FunctionalInterface
public interface Sharpener {
    Pencil pencil();

    default Pencil sharpen(){
        if(pencil().length() > 0){
            return pencil().newPencil(pencil().paper(), pencil().maxDurability(), pencil().length() - 1, pencil().eraserDurability(), pencil().maxDurability());
        }else{
            return pencil().newPencil(pencil().paper(), pencil().durability(), pencil().length(), pencil().eraserDurability(), pencil().maxDurability());
        }
    }
}
