# Deadwood
## By Malik Standley & Joshua L. Cary
### CSCI 345 Winter 2026
In the board game Deadwood, gameplay is structured around a series of four distinct days. Each day concludes when only a single film remains in play. At this point, the final film does not finish, and players return to the trailers as the map is reset for the next day.

Currently, everything is implemented.

# Links
#### [Class Diagram](https://docs.google.com/document/d/1lDRwtjGfXOVCj1rF3nigKJXXSnu5qpwhnqIu_Vciy40/edit?usp=sharing)
#### [Game Rules](https://drive.google.com/file/d/1x2tW7wT1uS7JvSGMbVKOgrHRjV-B2ulG/view?usp=sharing)
#### [Game Board (All)](https://drive.google.com/file/d/1u8y3hhRZ2XPxK-6-Kv8pc9Um_hXh3P8J/view?usp=sharing)
#### [Game Board (Pieces)](https://drive.google.com/drive/u/1/folders/14mSer8n8lkwHdqbKg6WG2HLuKxXBG6OT)

# Compiling the Report
The LaTeX report file in this project is `DeadwoodReport.tex`.

If `pdflatex` is already installed and on your PATH, compile the report from the project folder with:

```sh
pdflatex -interaction=nonstopmode DeadwoodReport.tex
```

If that command says `pdflatex: command not found`, install BasicTeX first:

```sh
brew install --cask basictex
sudo installer -pkg /opt/homebrew/Caskroom/basictex/2026.0301/mactex-basictex-20260301.pkg -target /
```

Then open a new terminal window and run:

```sh
cd /Users/malikstandley/Csci/25-26/winter26/cs345/Deadwood
/usr/local/texlive/2026basic/bin/universal-darwin/pdflatex -interaction=nonstopmode DeadwoodReport.tex
```

This generates `DeadwoodReport.pdf` in the same folder.
