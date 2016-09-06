package cucumber.runtime;

import gherkin.pickles.Pickle;
import gherkin.pickles.PickleTag;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class TagPredicateOld implements PicklePredicate {
    private TagExpressionOld tagExpression;

    public TagPredicateOld(List<String> tags) {
        this.tagExpression = new TagExpressionOld(tags);
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
        if (tagExpression.evaluate(pickleTags)) {
            return true;
        }
        return false;
    }

}
