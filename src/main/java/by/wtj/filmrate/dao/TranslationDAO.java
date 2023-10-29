package by.wtj.filmrate.dao;

import by.wtj.filmrate.dao.exception.DAOException;

public interface TranslationDAO {
    String getTranslationByLanguage(int textEntityID, String language)throws DAOException;
    String getTranslationByLanguageID(int textEntityID, int languageID)throws DAOException;
    int getOriginalLanguageID(int textEntityID) throws DAOException;
}
