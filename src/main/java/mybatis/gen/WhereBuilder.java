package mybatis.gen;

import java.util.Queue;

import mybatis.query.Part;
import mybatis.query.Part.Type;
import mybatis.query.PropertyPath;
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

			switch (type) {
				case BETWEEN:
					return root.andBetween(property.getSegment(), args.poll(), args.poll());
				case AFTER:
				case GREATER_THAN:
					return root.andGreaterThan(property.getSegment(), args.poll());
				case GREATER_THAN_EQUAL:
					return root.andGreaterThanOrEqualTo(property.getSegment(), args.poll());
				case BEFORE:
				case LESS_THAN:
					return root.andLessThan(property.getSegment(), args.poll());
				case LESS_THAN_EQUAL:
					return root.andLessThanOrEqualTo(property.getSegment(), args.poll());
				case IS_NULL:
					return root.andIsNull(property.getSegment());
				case IS_NOT_NULL:
					return root.andIsNotNull(property.getSegment());
				case NOT_IN:
					return root.andNotIn(property.getSegment(), (Iterable)args.poll());
				case IN:
					return root.andIn(property.getSegment(), (Iterable)args.poll());
//				case STARTING_WITH:
//				case ENDING_WITH:
//				case CONTAINING:
//				case NOT_CONTAINING:
//
//					if (property.getLeafProperty().isCollection()) {
//
//						Expression<Collection<Object>> propertyExpression = traversePath(root, property);
//						Expression<Object> parameterExpression = provider.next(part).getExpression();
//
//						// Can't just call .not() in case of negation as EclipseLink chokes on that.
//						return type.equals(NOT_CONTAINING) ? builder.isNotMember(parameterExpression, propertyExpression)
//								: builder.isMember(parameterExpression, propertyExpression);
//					}

				case LIKE:
					return root.andLike(property.getSegment(), args.poll().toString());
				case NOT_LIKE:
					return root.andNotLike(property.getSegment(), args.poll().toString());
//				case TRUE:
//					Expression<Boolean> truePath = getTypedPath(root, part);
//					return builder.isTrue(truePath);
//				case FALSE:
//					Expression<Boolean> falsePath = getTypedPath(root, part);
//					return builder.isFalse(falsePath);
				case SIMPLE_PROPERTY:
					return root.andEqualTo(property.getSegment(), args.poll());
//				case NEGATING_SIMPLE_PROPERTY:
//					return builder.notEqual(upperIfIgnoreCase(getTypedPath(root, part)),
//							upperIfIgnoreCase(provider.next(part).getExpression()));
				default:
					throw new IllegalArgumentException("Unsupported keyword " + type);
			}
		}


	}