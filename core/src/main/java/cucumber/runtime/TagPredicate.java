package cucumber.runtime;

import gherkin.pickles.Pickle;
import gherkin.pickles.PickleTag;
import io.cucumber.tagexpressions.Expression;
import io.cucumber.tagexpressions.TagExpressionParser;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class TagPredicate implements PicklePredicate {
    private final Expression expression;
    private final TagPredicateOld oldPredicate;

    public static TagPredicate create(List<String> tagExpressions) {
        if (TagExpressionOld.isOldTagExpression(tagExpressions)) {
            return new TagPredicate(null, new TagPredicateOld(tagExpressions));
        } else {
            return new TagPredicate(tagExpressions.isEmpty() ? null : tagExpressions.get(0));
        }
    }
    public TagPredicate(Expression expression) {
        this(expression, null);
    }

    public TagPredicate(String tagExpression) {
        this(tagExpression != null ? new TagExpressionParser().parse(tagExpression) : null);
    }

    private TagPredicate(Expression expression, TagPredicateOld oldPredicate) {
        this.expression = expression;
        this.oldPredicate = oldPredicate;
    }

    @Override
    public boolean apply(Pickle pickle) {
        List<PickleTag> tags;
        try { // TODO: Fix when Gherkin provide a getter for the tags.
            Field f;
            f = pickle.getClass().getDeclaredField("tags");
            f.setAccessible(true);
            tags = (List<PickleTag>) f.get(pickle);
        } catch (Exception e) {
            tags = Collections.<PickleTag>emptyList();
        }
        return apply(tags);
    }

    public boolean apply(Collection<PickleTag> pickleTags) {
        if (oldPredicate != null) {
            return oldPredicate.apply(pickleTags);
        } else if (expression == null) {
            return true;
        }
        List<String> tags = new ArrayList<String>();
        for (PickleTag pickleTag : pickleTags) {
            tags.add(pickleTag.getName());
        }
        return expression.evaluate(tags);
    }

}
