package com.cryptic.clientscripts.impl.dialogue.util;

import com.cryptic.model.entity.masks.impl.animations.Animation;
import lombok.Getter;

/**
 * Represents the expressions a chat dialogue head can make
 *
 * @author Arithium
 *
 */
@Getter
public enum Expression {

    NODDING_ONE(DialogueAnimationID.NODDING_ONE),
    NODDING_TWO(DialogueAnimationID.NODDING_TWO),
    DULL(DialogueAnimationID.DULL),
    DULL_TWO(DialogueAnimationID.DULL_TWO),
    NODDING_THREE(DialogueAnimationID.NODDING_THREE),
    NODDING_FIVE(DialogueAnimationID.NODDING_FIVE),
    SHAKING_HEAD_ONE(DialogueAnimationID.SHAKING_HEAD_ONE),
    SHAKING_HEAD_THREE(DialogueAnimationID.SHAKING_HEAD_THREE),
    NODDING_FOUR(DialogueAnimationID.NODDING_FOUR),
    NODDING(DialogueAnimationID.NODDING),
    SHAKING_HEAD_TWO(DialogueAnimationID.SHAKING_HEAD_TWO),
    H(DialogueAnimationID.H),
    H1(DialogueAnimationID.H1),
    HAPPY(DialogueAnimationID.HAPPY),
    ANXIOUS(DialogueAnimationID.ANXIOUS),
    CALM_TALK(DialogueAnimationID.CALM_TALK),
    DEFAULT(DialogueAnimationID.DEFAULT),
    EVIL(DialogueAnimationID.EVIL),
    BAD(DialogueAnimationID.BAD),
    WICKED(DialogueAnimationID.WICKED),
    ANNOYED(DialogueAnimationID.ANNOYED),
    DISTRESSED(DialogueAnimationID.DISTRESSED),
    AFFLICTED(DialogueAnimationID.AFFLICTED),
    DRUNK_LEFT(DialogueAnimationID.DRUNK_LEFT),
    DRUNK_RIGHT(DialogueAnimationID.DRUNK_RIGHT),
    NOT_INTERESTED(DialogueAnimationID.NOT_INTERESTED),
    SLEEPY(DialogueAnimationID.SLEEPY),
    PLAIN_EVIL(DialogueAnimationID.PLAIN_EVIL),
    LAUGH(DialogueAnimationID.LAUGH),
    SNICKER(DialogueAnimationID.SNICKER),
    HAVE_FUN(DialogueAnimationID.HAVE_FUN),
    GUFFAW(DialogueAnimationID.GUFFAW),
    EVIL_LAUGH_SHORT(DialogueAnimationID.EVIL_LAUGH_SHORT),
    SLIGHTLY_SAD(DialogueAnimationID.SLIGHTLY_SAD),
    SAD(DialogueAnimationID.SAD),
    VERY_SAD(DialogueAnimationID.VERY_SAD),
    ON_ONE_HAND(DialogueAnimationID.ON_ONE_HAND),
    ALMOST_CRYING(DialogueAnimationID.ALMOST_CRYING),
    NEARLY_CRYING(DialogueAnimationID.NEARLY_CRYING),
    ANGRY(DialogueAnimationID.ANGRY),
    FURIOUS(DialogueAnimationID.FURIOUS),
    ENRAGED(DialogueAnimationID.ENRAGED),
    MAD(DialogueAnimationID.MAD),
    OLM(DialogueAnimationID.OLM),
    OLM_LAUGH(DialogueAnimationID.OLM_LAUGH),
    PHOENIX(DialogueAnimationID.PHEONIX);


    /**
     * The DialogueExpression constructor.
     * @param animationId    The id of the animation for said expression.
     */
    Expression(int animationId) {
        animation = new Animation(animationId);
    }

    /**
     * The animation the dialogue head model will perform.
     * -- GETTER --
     *  Gets the animation for dialogue head model to perform.
     *
     * @return    animation.

     */
    private final Animation animation;

}
