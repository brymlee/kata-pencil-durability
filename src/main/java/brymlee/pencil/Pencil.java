package brymlee.pencil;

import brymlee.pencil.internals.Paper;

public class Pencil implements brymlee.pencil.internals.PencilInterface {

    private Integer maxDurability;
    private Integer durability;
    private Paper paper;
    private Integer length;

    private Pencil(){

    }

    private Pencil(final Integer durability,
                   final Paper paper,
                   final Integer length){
        this.maxDurability = durability;
        this.durability = durability;
        this.paper = paper;
        this.length = length;
    }

    public static Pencil create(final Integer durability,
                                final Paper paper,
                                final Integer length) {
        return new Pencil( durability, paper, length);
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
}
