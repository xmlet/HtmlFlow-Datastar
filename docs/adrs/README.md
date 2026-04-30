# Architectural Decision Records (ADRs)

This section tracks significant architectural decisions made in this project.

Each ADR documents the context, decision, and consequences of a specific technical choice.

Status indicates the outcome of the decision.


## Active ADRs
| ADR                                                     | Status   | Summary                                                                                                 |
|---------------------------------------------------------|----------|---------------------------------------------------------------------------------------------------------|
| [ADR-001](./ADR-001-gradle-plugin-expressions.md)       | Rejected | Reject Gradle plugin for DataStar expression transpilation.                                             |
| [ADR-002](./ADR-002-expression-registration.md)         | Accepted | Moved the logic that generate DataStarExpression´s to the ExpressionBuilder                             |
| [ADR-003](./ADR-003-signal-value.md)                    | Accepted | Remove the property value of Signal                                                                     |
| [ADR-004](./ADR-004-datastar-expression-restructure.md) | Accepted | Refactor DataStarExpression as a sealed hierarchy with Signal, DataStarAction, and DataStarExpressionOp |
  
