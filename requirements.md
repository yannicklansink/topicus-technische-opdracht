# Requirements - VipLive Lab Planningsapplicatie

## Originele requirements (uit casus)

### Functioneel
- Er is één agenda om afspraken in weg te schrijven
- Afspraken mogen elkaar niet overlappen
- Het inplannen van een nieuwe afspraak
- Het opvragen van het eerstvolgende vrije moment waarop een afspraak ingepland zou kunnen worden

### Technisch
- REST API als back-end
- Kotlin als programmeertaal
- Spring Boot als framework

### Eigen invulling (uit casus)
De casus geeft aan dat de volgende zaken zelf ingevuld mogen worden:
- Object-structuur voor agenda en afspraken
- Input en output van de API
- Omgang met foutmeldingen

---

## Eigen beslissingen en aannames

### Afspraak-eigenschappen
| Beslissing | Keuze |
|------------|-------|
| Duur van afspraken | Vast: 30 minuten |
| Afspraak-gegevens | starttijd, titel, beschrijving (optioneel), naam persoon |
| ID-type | Auto-increment (Long) |

### Agenda-beperkingen
| Beslissing | Keuze |
|------------|-------|
| Werkdagen | Alleen maandag t/m vrijdag |
| Werkuren | 09:00 - 17:00 |
| Laatste starttijd | 16:30 (zodat afspraak om 17:00 eindigt) |

### Extra functionaliteit (beyond casus)
| Functionaliteit | Beschrijving |
|-----------------|--------------|
| Alle afspraken ophalen | Lijst van alle afspraken |
| Afspraken per dag | Filteren op specifieke datum |
| Afspraak ophalen | Specifieke afspraak op ID |
| Afspraak wijzigen | Alleen tijdstip mag gewijzigd worden |
| Afspraak verwijderen | Verwijderen op ID |

### Validatieregels
| Regel | Beschrijving |
|-------|--------------|
| Geen overlap | Nieuwe/gewijzigde afspraken mogen niet overlappen met bestaande |
| Alleen werkdagen | Afspraken alleen op ma-vr |
| Binnen werkuren | Afspraken tussen 09:00 en 17:00 |
| Niet in verleden | Afspraken in het verleden kunnen niet gewijzigd of verwijderd worden |

