package org.ourgrid.common.specification.main;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.Map.Entry;

import condor.classad.ClassAdParser;
import condor.classad.Expr;
import condor.classad.ListExpr;
import condor.classad.RecordExpr;

/**
 * This class maps worker attributes defined in SDF file, and the ones collected dynamically, to GLUE tags specified in the
 * tags.conf file. 
 * @author David Candeia Medeiros Maia
 *
 */
public class JDLTagsPublisher {
	
	private static final String PERIOD_REGEX = "\\.";
	private static final String ARRAY_SYMBOL = "*";
	private static Map<String, String> tagsProperties = new TreeMap<String, String>();
	
	/**
	 * It compiles a {@link String} expression into a {@link RecordExpr} and inserts all attributes to publish
	 * according to the mappings specified at the tags configuration file.
	 * 
	 * @param classAdExpression
	 * @return
	 */
	public static RecordExpr buildExprWithTagsToPublish( String classAdExpression ) {

		Set<Entry<String,String>> entrySet = tagsProperties.entrySet();
		RecordExpr expr = (RecordExpr) new ClassAdParser(classAdExpression).parse();
		
		for (Entry<String, String> entry : entrySet) {
			String[] key = entry.getKey().trim().split(PERIOD_REGEX);
			String[] values = entry.getValue().split(", *");
			Stack<RecordExpr> stack = new Stack<RecordExpr>();
			stack.push(expr);
			mapAttributes(stack, expr, new ArrayList<String>(Arrays.asList(key)), values);
		}
		return expr;
	}

	/**
	 * This method is responsible for loading the configuration file that contains the mapping of SDF constants and GLUE
	 * tags
	 */
	public static void loadGLUETags(String tagFilePath){

		/** Get an abstraction for the properties file */
		File propertiesFile = new File( tagFilePath );

		/* load the properties file, if it exists */
		try {
			Properties properties = new Properties();
			properties.load( new FileInputStream( propertiesFile ) );
			reloadProperties(properties);
		} catch (Exception e) {
			throw new RuntimeException("Cannot load tags from file " + tagFilePath, e);
		}
	}
	
	/**
	 * This method changes the keys of the tags properties stored to lower case values.
	 * @param properties The properties loaded from the tags.conf file
	 */
	private static void reloadProperties(Properties properties) {
		Set<Entry<Object,Object>> entrySet = properties.entrySet();
		for (Entry<Object, Object> entry : entrySet) {
			String key = ((String) entry.getKey()).toLowerCase();
			String value = ((String) entry.getValue()).toLowerCase();
			tagsProperties.put(key, value);
		}
		validateTags();
	}

	/**
	 * This method validates if the properties defined in the tags.conf file are defined correctly. Basically,
	 * it's checked if the number of levels are defined equally and if each level mapping is defined separately.
	 */
	private static void validateTags() {
		Set<Entry<String,String>> entrySet = tagsProperties.entrySet();
		for (Entry<String, String> entry : entrySet) {
			String key = entry.getKey();
			String[] values = entry.getValue().split(", *");
			String[] splitKey = key.split(PERIOD_REGEX);
			for (String value : values) {
				if(value.endsWith(ARRAY_SYMBOL)){
					if(value.indexOf('.') != -1){
						throw new RuntimeException("Array value " + value + "must be on Ad root." );
					}
				}else{
					if(value.trim().split(PERIOD_REGEX).length != splitKey.length){
						throw new RuntimeException("Value " + value + " has a different nesting depth of key " + entry.getKey() );
					}
				}
			}
			if(splitKey.length > 1){
				String keyPiece = key.substring(0, key.lastIndexOf("."));
				String keyPieceValue = tagsProperties.get(keyPiece);
				if(keyPieceValue == null){
					throw new RuntimeException("Missing tag map for " + keyPiece);
				}
				String[] keyPieceValues = keyPieceValue.split(", *");
				for (String value : values) {
					if(!value.endsWith(ARRAY_SYMBOL)){
						String valuePiece = value.substring(0, value.lastIndexOf("."));
						boolean find = false;
						for (String pieceValue : keyPieceValues) {
							if(valuePiece.trim().equalsIgnoreCase(pieceValue.trim())){
								find = true;
							}
						}
						if(!find){
							throw new RuntimeException("Missing tag map for " + value + "in ");
						}
					}
				}
			}
		}
	}
	
	/**
	 * This method is responsible for mapping a certain SDF key to a set of values defined in the tags.conf file
	 * @param stack A stack containing all record expressions through a certain evaluation since the root ClassAd until the current one
	 * @param expr The expression at which the key will be searched and mapped
	 * @param key The name of the attribute to be mapped
	 * @param values The set of mapping values for the key
	 */
	@SuppressWarnings("unchecked")
	private static void mapAttributes(Stack<RecordExpr> stack, RecordExpr expr,
			List<String> key, String[] values) {

		if(key.size() == 1 && expr != null){
			Expr current = expr.lookup(key.get(0));
			if(current != null){
				for (String value : values) {
					String trim = value.trim();
					if(trim.endsWith(ARRAY_SYMBOL)){
						ListExpr list = getListAtRoot(stack, trim.substring(0, trim.length()-1));
						list.add(current);
					}else{
						String[] split = trim.split(PERIOD_REGEX);
						expr.insertAttribute(split[split.length - 1].trim(), current);
					}
				}
			}
		}else if(expr != null){
			Expr current = ((RecordExpr) expr).lookup(key.get(0));
			if(current != null){
				key.remove(0);
				for (int i = 0; i < values.length; i++) {
					String value = values[i];
					if(!value.endsWith(ARRAY_SYMBOL)){
						String baseName = value.substring(0, value.indexOf('.'));
						values[i] = value.substring(value.indexOf('.')+1, value.length());
						Expr currentValue = ((RecordExpr) expr).lookup(baseName);
						if(currentValue == null){
							throw new RuntimeException();
						}
					}
				}
				stack.push(expr);
				if(current.type == Expr.LIST){
					ListExpr currentList = (ListExpr) current;
					Iterator<Expr> iterator = currentList.iterator();
					while (iterator.hasNext()) {
						Expr child = iterator.next();
						if(child.type == Expr.ATTRIBUTE){
							Expr childExpr = lookInStack(stack, child.toString());
							mapAttributes(stack, (RecordExpr) childExpr, key, values);
						}else{
							mapAttributes(stack, (RecordExpr) child, key, values);
						}
					}
				}else if (current.type == Expr.ATTRIBUTE){
					Expr childExpr = lookInStack(stack, current.toString());
					mapAttributes(stack, (RecordExpr) childExpr, key, values);
				}else{
					mapAttributes(stack, (RecordExpr) current, key, values);
				}
				stack.pop();
			}
		}
	}
	
	/**
	 * This method searches for a certain attribute in the root ClassAd expression being considered.
	 * @param stack A stack containing all record expressions through a certain evaluation since the root ClassAd until the current one
	 * @param attribute The attribute being searched
	 * @return The attribute value, if existent, and null otherwise
	 */
	private static Expr lookInStack(Stack<RecordExpr> stack, String attribute) {
		for (RecordExpr expr : stack) {
			Expr value = expr.lookup(attribute);
			if(value != null){
				return value;
			}
			
		}
		return null;
	}

	/**
	 * This method searches for a certain list attribute in the root ClassAd expression
	 * @param stack A stack containing all record expressions through a certain evaluation since the root ClassAd until the current one
	 * @param listName The name of the list attribute being searched
	 * @return The value of the list attribute being searched
	 */
	private static ListExpr getListAtRoot(Stack<RecordExpr> stack, String listName) {
		RecordExpr root = stack.lastElement();
		Expr lookup = root.lookup(listName);
		if(lookup == null){
			root.insertAttribute(listName, new ListExpr());
			lookup = root.lookup(listName);
		}
		ListExpr list = (ListExpr) lookup;
		return list;
	}
}