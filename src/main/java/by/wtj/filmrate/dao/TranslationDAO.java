package by.wtj.filmrate.dao;

import by.wtj.filmrate.bean.Language;
import by.wtj.filmrate.bean.TextEntity;
import by.wtj.filmrate.dao.exception.DAOException;

import java.util.List;

public interface TranslationDAO {
    String getTranslationByLanguage(int textEntityID, String language)throws DAOException;
    String getTranslationByLanguageID(int textEntityID, int languageID)throws DAOException;
    int getOriginalLanguageID(int textEntityID) throws DAOException;

    List<Language> getAllLanguages()throws DAOException;
    List<TextEntity> getTextEntities(List<Integer> textEntitiesIDs)throws DAOException;
}
