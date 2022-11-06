package dabba.doo.annotationprocessor.db.sqlwriter;

import dabba.doo.annotationprocessor.core.annotations.entity.J2dColumn;
import dabba.doo.annotationprocessor.core.annotations.entity.J2dEntity;
import dabba.doo.annotationprocessor.core.annotations.entity.J2dId;
import dabba.doo.annotationprocessor.core.reflection.ClassReflectionTool;

import javax.lang.model.element.TypeElement;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SqlWriteSentenceGenerator class
 * Util to generate sql sentences to write to the database
 * @author josue.rojas
 */
public class SqlWriteSentenceGenerator {

    public static String writeDeleteSentence(Class<?> clazz) {
        final StringBuilder deleteSentenceStringBuilder = new StringBuilder("delete from ").append(clazz.getAnnotation(J2dEntity.class).tableName());
        final StringBuilder whereStringBuilder = new StringBuilder("where ");

        final String whereSentence = Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(J2dId.class) && field.isAnnotationPresent(J2dColumn.class))
                .map(field -> field.getAnnotation(J2dColumn.class).name())
                .map(field -> new StringBuilder(field).append("=:").append(field).toString())
                .collect(Collectors.joining(","));

        return deleteSentenceStringBuilder.append(" where ").append(whereSentence).toString();
    }

    public static String writeDeleteSentence(final TypeElement clazz) {
        final StringBuilder deleteSentenceStringBuilder = new StringBuilder("delete from ").append(clazz.getAnnotation(J2dEntity.class).tableName());
        final StringBuilder whereStringBuilder = new StringBuilder("where ");

        final String whereSentence = ClassReflectionTool.getDeclaredFieldsByType(clazz).stream()
                .filter(field -> field.getAnnotation(J2dId.class) != null && field.getAnnotation(J2dColumn.class) != null)
                .map(field -> field.getAnnotation(J2dColumn.class).name())
                .map(field -> new StringBuilder(field).append("=:").append(field).toString())
                .collect(Collectors.joining(","));

        return deleteSentenceStringBuilder.append(" where ").append(whereSentence).toString();
    }

    public static String writeInsertSentence(final Class<?> clazz)
    {
        final StringBuilder insertStringBuilder = new StringBuilder("insert into ").append(clazz.getAnnotation(J2dEntity.class).tableName()).append("(");
        final StringBuilder valuesStringBuilder = new StringBuilder("values(");

        // add insert columns
        final List<String> fields =
                Arrays.stream(clazz.getDeclaredFields())
                        .filter(field -> field.isAnnotationPresent(J2dColumn.class) && !field.isAnnotationPresent(J2dId.class))
                        .map(field -> field.getAnnotation(J2dColumn.class).name())
                        .collect(Collectors.toList());

        for (int i = 0; i < fields.size(); i++) {
            final String columnName = fields.get(i);
            insertStringBuilder.append(columnName).append((i < fields.size() - 1 ? ", " : ")"));
            valuesStringBuilder.append(":").append(columnName).append((i < fields.size() - 1 ? ", " : ")"));
        }

        return insertStringBuilder.append(" ").append(valuesStringBuilder).toString();
    }

    public static String writeInsertSentence(final TypeElement clazz)
    {
        final StringBuilder insertStringBuilder = new StringBuilder("insert into ").append(clazz.getAnnotation(J2dEntity.class).tableName()).append("(");
        final StringBuilder valuesStringBuilder = new StringBuilder("values(");

        // add insert columns
        final List<String> fields =
                ClassReflectionTool.getDeclaredFieldsByType(clazz).stream()
                        .filter(field -> field.getAnnotation(J2dColumn.class) != null && field.getAnnotation(J2dId.class) == null)
                        .map(field -> field.getAnnotation(J2dColumn.class).name())
                        .collect(Collectors.toList());

        for (int i = 0; i < fields.size(); i++) {
            final String columnName = fields.get(i);
            insertStringBuilder.append(columnName).append((i < fields.size() - 1 ? ", " : ")"));
            valuesStringBuilder.append(":").append(columnName).append((i < fields.size() - 1 ? ", " : ")"));
        }

        return insertStringBuilder.append(" ").append(valuesStringBuilder).toString();
    }

    public static String writeUpdateSentence(final Class<?> clazz) {

        final StringBuilder updateStringBuilder = new StringBuilder("update ").append(clazz.getAnnotation(J2dEntity.class).tableName()).append("(");
        final StringBuilder setStringBuilder = new StringBuilder("set ");

        final List<String> fields =
                Arrays.stream(clazz.getDeclaredFields())
                        .filter(field -> field.isAnnotationPresent(J2dColumn.class) && !field.isAnnotationPresent(J2dId.class))
                        .map(field -> field.getAnnotation(J2dColumn.class).name())
                        .collect(Collectors.toList());

        for (int i = 0; i < fields.size(); i++) {
            final String columnName = fields.get(i);
            updateStringBuilder.append(columnName).append((i < fields.size() - 1 ? ", " : ")"));
            setStringBuilder.append(":").append(columnName).append((i < fields.size() - 1 ? ", " : ""));
        }

        final StringBuilder whereStringBuilder = new StringBuilder(" ");
        final String idField = ClassReflectionTool.getIdField(clazz).getAnnotation(J2dColumn.class).name();
        whereStringBuilder.append("where ").append(idField).append(" =:" + idField);

        return updateStringBuilder.append(" ").append(setStringBuilder).append(" ").append(whereStringBuilder).toString();
    }

    public static String writeUpdateSentence(final TypeElement clazz) {

        final StringBuilder updateStringBuilder = new StringBuilder("update ").append(clazz.getAnnotation(J2dEntity.class).tableName()).append(" ");
        final StringBuilder setStringBuilder = new StringBuilder("set ");

        final List<String> fields =
                ClassReflectionTool.getDeclaredFieldsByType(clazz).stream()
                        .filter(field -> field.getAnnotation(J2dColumn.class)!=null && field.getAnnotation(J2dId.class) == null)
                        .map(field -> field.getAnnotation(J2dColumn.class).name())
                        .collect(Collectors.toList());

        for (int i = 0; i < fields.size(); i++) {
            final String columnName = fields.get(i);
            setStringBuilder.append(columnName).append(" =:").append(columnName).append((i < fields.size() - 1 ? ", " : ""));
        }

        final StringBuilder whereStringBuilder = new StringBuilder(" ");
        final String idField = ClassReflectionTool.getIdField(clazz).getAnnotation(J2dColumn.class).name();
        whereStringBuilder.append("where ").append(idField).append(" =:").append(idField);

        return updateStringBuilder.append(" ").append(setStringBuilder).append(" ").append(whereStringBuilder).toString();
    }
}