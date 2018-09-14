package brymlee.pencil;

import brymlee.pencil.internals.Paper;

public class Pencil implements brymlee.pencil.internals.Pencil {

    private Integer maxDurability;
    private Integer durability;
    private Paper paper;

    private Pencil(){

    }

    private Pencil(final Integer durability,
                   final Paper paper){
        this.maxDurability = durability;
        this.durability = durability;
        this.paper = paper;
    }

    public static Pencil create(final Integer degredation,
                                final Paper paper) {
        return new Pencil(degredation, paper);
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
}
