grammar MyLang;

/* ----------------------------------------------------------------------------
   OPTIONS/HEADER
   Adjust package names and imports to match your project’s structure.
---------------------------------------------------------------------------- */

@header {
package step5cfg;

import step5cfg.AllSemantics.Environment;
import step5cfg.AllSemantics.*;
import step5cfg.AllSemantics.Number;
import step5cfg.AllSemantics.Boolean;
}

@members {
// If you need imports or static references, place them here.
// For example:

// import com.example.parser.ast.*;
// import com.example.parser.ast.Boolean; // if "Boolean" is a class in your AST
}

/* ----------------------------------------------------------------------------
   PARSER RULES
---------------------------------------------------------------------------- */

start
  returns [Statement value]
  : s=statement EOF
    {
      $value = $s.value;
    }
  ;

statement
  returns [Statement value]
  : s=sequence
    {
      $value = $s.value;
    }
  ;

sequence
  returns [Statement value]
  : first=sequenced_statement
    (
       second=sequence
       {
         $value = new Sequence($first.value, $second.value);
       }
    )?
    {
      // If there's no second=sequence matched,
      // the sequence is just the single statement:
      if ($value == null) {
        $value = $first.value;
      }
    }
  ;

sequenced_statement
  returns [Statement value]
  : w=while_statement       { $value = $w.value; }
  | a=assign_statement      { $value = $a.value; }
  | i=if_statement          { $value = $i.value; }
  | d=do_nothing_statement  { $value = $d.value; }
  ;

/* ----------------------------------------------------------------------------
   Individual statement rules
---------------------------------------------------------------------------- */

while_statement
  returns [While value]
  : 'while' '(' cond=expression ')' '{' body=statement '}'
    {
      $value = new While($cond.value, $body.value);
    }
  ;

assign_statement
  returns [Assign value]
  : n=IDENT '=' expr=expression ';'
    {
      $value = new Assign($n.text, $expr.value);
    }
  ;

if_statement
  returns [If value]
  : 'if' '(' cond=expression ')' '{' consequence=statement '}' 'else' '{' alternative=statement '}'
    {
      $value = new If($cond.value, $consequence.value, $alternative.value);
    }
  ;

do_nothing_statement
  returns [Statement value]
  : 'do-nothing' (';' )?
    {
      $value = new DoNothing();
    }
  ;

/* ----------------------------------------------------------------------------
   Expressions
---------------------------------------------------------------------------- */

/**
 * expression = less_than
 */
expression
  returns [Expression value]
  : lt=less_than
    {
      $value = $lt.value;
    }
  ;

less_than
  returns [Expression value]
  : left=add '<' right=less_than
    {
      $value = new LessThan($left.value, $right.value);
    }
  | add
    {
      $value = $add.value;
    }
  ;

add
  returns [Expression value]
  : left=multiply '+' right=add
    {
      $value = new Add($left.value, $right.value);
    }
  | multiply
    {
      $value = $multiply.value;
    }
  ;


multiply
  returns [Expression value]
  : left=brackets '*' right=multiply
    {
      $value = new Mult($left.value, $right.value);
    }
  | brackets
    {
      $value = $brackets.value;
    }
  ;

brackets
  returns [Expression value]
  : '(' e=expression ')'
    {
      $value = $e.value;
    }
  | t=term
    {
      $value = $t.value;
    }
  ;

term
  returns [Expression value]
  : n=number  { $value = $n.value; }
  | b=boolean { $value = $b.value; }
  | v=variable { $value = $v.value; }
  ;

/* ----------------------------------------------------------------------------
   Leaf nodes: number, boolean, variable
---------------------------------------------------------------------------- */

number
  returns [Expression value]
  : DIGITS
    {
      int val = Integer.parseInt($DIGITS.text);
      $value = new Number(val);
    }
  ;

boolean
  returns [Expression value]
  : 'true'
    {
      // Suppose your AST class name is also "Boolean"
      $value = new Boolean(true);
    }
  | 'false'
    {
      $value = new Boolean(false);
    }
  ;

variable
  returns [Expression value]
  : IDENT
    {
      $value = new Variable($IDENT.text);
    }
  ;

/* ----------------------------------------------------------------------------
   LEXER RULES
---------------------------------------------------------------------------- */

IDENT
  : [a-z]+
  ;

/** Matches integer digits. */
DIGITS
  : [0-9]+
  ;

/** Whitespace and line breaks. Skip them so they don’t appear in the parser. */
WS
  : [ \t\r\n]+ -> skip
  ;
