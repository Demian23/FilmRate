package by.wtj.filmrate.dao;

import by.wtj.filmrate.bean.Language;
import by.wtj.filmrate.bean.TextEntity;
import by.wtj.filmrate.bean.Translation;
import by.wtj.filmrate.dao.exception.DAOException;

import java.util.List;

public interface TranslationDAO {
    void getTranslation(Translation translation)throws DAOException;
    int getOriginalLanguageID(int textEntityID) throws DAOException;

    List<Language> getAllLanguages()throws DAOException;
}
