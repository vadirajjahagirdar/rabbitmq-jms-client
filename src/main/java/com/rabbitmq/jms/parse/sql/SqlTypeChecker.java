/* Copyright (c) 2013 Pivotal Software, Inc. All rights reserved. */
package com.rabbitmq.jms.parse.sql;

import static com.rabbitmq.jms.parse.ParseTreeTraverser.traverse;

import java.util.Collections;
import java.util.Map;

import com.rabbitmq.jms.parse.ParseTree;

public abstract class SqlTypeChecker { // prevent instantiation   directly
    private SqlTypeChecker() {}        // prevent instantiation indirectly

    public static SqlExpressionType deriveExpressionType(ParseTree<SqlTreeNode> tree, Map<String, SqlExpressionType> identifierType) {
        if (tree == null) return SqlExpressionType.NOT_SET; // null trees are not of any type
        traverse(tree, new SqlTypeSetterVisitor(identifierType));
        return tree.getNode().getExpValue().getType();
    }

    public static SqlExpressionType deriveExpressionType(ParseTree<SqlTreeNode> tree) {
        return deriveExpressionType(tree, Collections.<String, SqlExpressionType> emptyMap());
    }

}
