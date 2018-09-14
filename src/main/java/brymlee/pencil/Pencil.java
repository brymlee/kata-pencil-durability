package brymlee.pencil;

import brymlee.pencil.internals.Paper;

public class Pencil implements brymlee.pencil.internals.Pencil {

    private Integer degradation;
    private Paper paper;

    private Pencil(){

    }

    private Pencil(final Integer degradation,
                   final Paper paper){
        this.degradation = degradation;
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
    public Integer degradation() {
        return this.degradation;
    }
}
