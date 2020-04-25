package querymethods.util;

import java.util.Queue;

import querymethods.springdata.mapping.PropertyPath;
import querymethods.springdata.query.parser.Part;
import querymethods.springdata.query.parser.Part.Type;
import tk.mybatis.mapper.entity.Example.Criteria;

/**
 * where条件构建类 参考spring data PredicateBuilder
 * 
 * @author OYGD
 *
 */
public class WhereBuilder {

		/**
		 * 拼装where条件，参考spring data PredicateBuilder
		 * 
		 * @return
		 */
		public static Criteria build(Part part, Criteria root, Queue<Object> args) {

			PropertyPath property = part.getProperty();
			Type type = part.getType();
			String segment = property.getSegment();
			switch (type) {
				case BETWEEN:
					return root.andBetween(segment, args.poll(), args.poll());
				case AFTER:
				case GREATER_THAN:
					return root.andGreaterThan(segment, args.poll());
				case GREATER_THAN_EQUAL:
					return root.andGreaterThanOrEqualTo(segment, args.poll());
				case BEFORE:
				case LESS_THAN:
					return root.andLessThan(segment, args.poll());
				case LESS_THAN_EQUAL:
					return root.andLessThanOrEqualTo(segment, args.poll());
				case IS_NULL:
					return root.andIsNull(segment);
				case IS_NOT_NULL:
					return root.andIsNotNull(segment);
				case NOT_IN:
					return root.andNotIn(segment, (Iterable)args.poll());
				case IN:
					return root.andIn(segment, (Iterable)args.poll());
				case STARTING_WITH:
					return root.andLike(segment, "%" + args.poll().toString());
				case ENDING_WITH:
					return root.andLike(segment, args.poll().toString() + "%");
				case CONTAINING:
					return root.andLike(segment, "%" + args.poll().toString() + "%");
				case NOT_CONTAINING:
					return root.andNotIn(segment, (Iterable)args.poll());
				case LIKE:
					return root.andLike(segment, args.poll().toString());
				case NOT_LIKE:
					return root.andNotLike(segment, args.poll().toString());
				case TRUE:
					return root.andEqualTo(segment, true);
				case FALSE:
					return root.andEqualTo(segment, false);
				case SIMPLE_PROPERTY:
					return root.andEqualTo(segment, args.poll());
				case NEGATING_SIMPLE_PROPERTY:
					return root.andNotEqualTo(segment, args.poll());
				default:
					throw new IllegalArgumentException("Unsupported keyword " + type);
			}
		}


	}