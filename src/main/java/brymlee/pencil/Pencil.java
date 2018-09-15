package brymlee.pencil;

import brymlee.pencil.internals.Paper;

public class Pencil implements brymlee.pencil.internals.PencilInterface {

    private Integer eraserDurability;
    private Integer maxDurability;
    private Integer durability;
    private Paper paper;
    private Integer length;

    private Pencil(){

    }

    private Pencil(final Integer durability,
                   final Paper paper,
                   final Integer length,
                   final Integer eraserDurability){
        this.maxDurability = durability;
        this.durability = durability;
        this.paper = paper;
        this.length = length;
        this.eraserDurability = eraserDurability;
    }

    public static Pencil create(final Integer durability,
                                final Paper paper,
                                final Integer length,
                                final Integer eraserDurability) {
        return new Pencil( durability, paper, length, eraserDurability);
    }

    @Override
    public Paper paper() {
        return this.paper;
    }

    @Override
    public Integer durability() {
        return this.durability;
    }

    @Override
    public Integer maxDurability(){
        return this.durability;
    }

    @Override
    public Integer length() {
        return this.length;
    }

    @Override
    public Integer eraserDurability() {
        return this.eraserDurability;
    }
}
