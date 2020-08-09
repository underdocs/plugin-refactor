# Plugin Refactor

Ebben a tárolóban helyeztem el az ötletemet a beépülő modulos refaktorálásra vonatkozóan. Kiemelném, hogy ez közel sem egy teljes megvalósítás, csupán egy durva skicc, hogy mégis hogyan lehetne összerakni a rendszert.

A következőkben vázolnám, hogy a kódbázis egyes részei minek felelnek meg, miért jöttek létre, és miért pont úgy, ahogyan.

## Primitívek

Mielőtt leírnám a struktúrát alkotó legfontosabb elemeket, azokkal a primitívekkel kezdeném, melyekkel az Underdocs dolgozik. A legalsó szinten három primitívvel bírunk:

  * code: A kódbázis elemei. Lehet module, header, function stb.
  * page: Egy különálló markdown fájl.
  * resource: Valamilyen asset, mint például egy kép, egy SVG vagy egy CSS állomány.

Legfontosabb közös pontok:

  * Linkelhetőség (`Linkable` interfész és `Linker`): tetszőleges két primitív között képesek vagyunk relatív linket képezni. 
  * Feldolgozás: A primitívek minden példánya végigszalad valamilyen, a típusának megfelelő csővezetéken, ahol feldolgozásra kerül. Azaz, `PipelineItem` készül belőle.

## Csővezeték

Az ötlet lényegi része a csővezeték-alapú (pipeline) absztrakció: minden primitív típushoz tartozik egy-egy különálló csővezeték (hiszen különdöző feldolgozást igényelnek). Ha például a `code` típust tekintjük, akkor ez azt jelenti, hogy beolvassuk az összes kód elemet (legyen az module, header, function, struct, bármi), majd ezek mindegyikét végignyomjuk a csővezetéken.

Minden csővezeték fázisokból (`Stage` interfész) áll:

~~~~Kotlin
interface Stage<In, Out> {
    suspend fun process(input: In): StageOutput<Out>

    fun <NewOut> then(nextStage: Stage<Out, NewOut>): Stage<In, NewOut> {
        return CompositeStage<In, Out, NewOut>(this, nextStage)
    }
}
~~~~

A lelke ezen interfésznek természetesen a `process` metódus, mely kap egy bemeneti elemet, majd kiköp egy `StageOutput` példányt, ami becsomagolja a kimeneti elemet. A `StageOutput` szerepe, hogy

  * a fázisok tudjanak normális, példány-kimenetet adni,
  * a fázisok tudjanak `null` kimenetet adni,
  * a fázisok jelezhessék, ha a kimenetet nem kell továbbadni (`StageOutput.abortProcessing()`).

~~~~Kotlin
class StageOutput<out T>(val result: T) {
    companion object {
        private val ABORT_PROCESSING = StageOutput(null)

        fun <T> abortProcessing(): StageOutput<T> {
            @Suppress("UNCHECKED_CAST")
            return ABORT_PROCESSING as StageOutput<T>
        }
    }
}
~~~~

A fázisok összefűzhetők: az egyik  kimenete lehet egy következő fázis bemenete. Erről a `Stage.then` metódus gondoskodik:

~~~~Kotlin
fun <NewOut> then(nextStage: Stage<Out, NewOut>): Stage<In, NewOut> {
    return CompositeStage<In, Out, NewOut>(this, nextStage)
}
~~~~

Ha a típusokat tekintjük, akkor e metódus felhasználásával egy 

~~~~
In -> Out -> NewOut
~~~~

csővezeték hozható létre. Természetesen, az így kapott csővezeték is megvalósítja a `Stage` interfészt, tehát újra alkalmazható a `then` metódus egy hosszabb csővezeték létrehozásához. Példa: `CodePipeline` osztály.

E pakolgatás hátterében a `CompositeStage` osztály dolgozik:

~~~~Kotlin
class CompositeStage<In, Mid, Out>(
    private val first: Stage<In, Mid>,
    private val second: Stage<Mid, Out>
) : Stage<In, Out> {
    override suspend fun process(input: In): StageOutput<Out> {
        val firstOutput = first.process(input)

        return if (firstOutput === StageOutput.abortProcessing<Mid>()) {
            StageOutput.abortProcessing()
        } else {
            second.process(firstOutput.result)
        }
    }
}
~~~~

Az ötlet nagyon egyszerű: egy kétfázisú csővezeték tulajdonképpen egy olyan fázis, aminek a bemeneti (`In`) típusa az első fázis bemeneti típusa (`first`), kimeneti típusa (`Out`) pedig a második fázis kimeneti típusa (`second`). A megvalósítás lényege pedig a köztes típus (`Mid`), aminek nyilván egyeznie kell.

Mivel egy a kétfázisú csővezeték is egy fázis, ezért ezen is alkalmazhatjuk a `then` metódust. Tehát minden, egy fázisnál hosszabb csővezeték valójában kétfázisú, ezek a fázisok azonban további fázisokra bomolhatnak szét. Azaz, ha például szeretnénk készíteni egy `Stage1 -> Stage2 -> Stage3 -> Stag4` formájú csővezetéket, akkor a következő két megoldás ekvivalens:

~~~~Kotlin
Stage1()
  .then(Stage2())
  .then(Stage3())
  .then(Stage4())
~~~~

~~~~Kotlin
CompositeStage(
    CompositeStage(
        CompositeStage(
            Stage1,
            Stage2
        ),
        Stage3
    ),
    Stage4
)
~~~~

Amit még érdemes itt megjegyeznünk, az a `Stage.process` metóduson a `suspend` kulcsszó. Ez hasonló a JavaScript `await`-jéhez, és a korutinokkal van kapcsolatban. Mivel ez egy `suspend` metódus, ezért meghívhatók benne más `suspend` metódusok is, és nekünk ez a legfontosabb.

Példa pipeline: a `CodePipeline` és az `underdocs.refactor.pipeline.code` csomag. Vegyük észre a `Stage` megvalósításokban az `Input` és `Output` osztályokat!

## Pluginok

Az egyes csővezetékek tehát fázisokra bomlanak szét. E fázisok általában pluginokat hajtanak meg, melyeknek lehetséges típusai az `underdocs.refactor.plugin` csomagban találhatók.

Tekintsük például a `MarkdownProcessor` interfészt!

~~~~Kotlin
interface MarkdownProcessor {
    fun beforeMarkdownParse(beforeContext: BeforeContext): StepOutput<String> =
        StepOutput.skip()

    fun afterMarkdownParse(afterContext: AfterContext): StepOutput<MarkdownDocument> =
        StepOutput.skip()

    class BeforeContext(val element: CodeElement, val document: String)

    class AfterContext(val element: CodeElement, val document: MarkdownDocument)
}
~~~~

Két metódust láthatunk, a `beforeMarkdownParse` a `String -> MarkdownDocument` parse-olás előtt lesz meghívva, míg az `afterMarkdownParse` azt követően. Mindegyik metódus bemenete egy megfelelő `Context` típus, ami magában foglal mindent, amivel a megfelelő függvénynek dolgoznia kell. E `Context` osztályoknak az előnye, hogy nem lesz háromezer paramétere a függvénynek akkor sem, ha sok dolgot kell átadnunk nekik. Összességében egyszerűbbé teszik a paraméterekkel való munkát.

A metódusok kimenete pedig `StepOutput` típussal. Vegyük észre itt a párhuzamot a `StageOutput` típussal! Ennek oka, hogy a csővezeték fázisokból áll, míg minden fázis lépésekből. Egy lépésnek pedig egy pluginvégrehajtás felel meg (ez jelenik meg az `underdocs.refactor.pipeline.code` fázisaiban, melyek megfelelő pluginokat várnak paraméterként), ezért lesz `StepOutput` a kimenet.

~~~~Kotlin
class StepOutput<out T>(val result: T?) {
    companion object {
        private val ABORT_PROCESSING = StepOutput(null)
        private val SKIP = StepOutput(null)

        fun <T> abortProcessing(): StepOutput<T> {
            @Suppress("UNCHECKED_CAST")
            return ABORT_PROCESSING as StepOutput<T>
        }

        fun <T> skip(): StepOutput<T> {
            @Suppress("UNCHECKED_CAST")
            return SKIP as StepOutput<T>
        }
    }
}
~~~~

A szemantika itt kiegészül a `StepOutput.skip()` metódussal, mely azt jelenti, hogy a plugin nem szeretne semmit sem csinálni a bemenettel.

Példa implementáció: `ExampleHtmlProcessor`.

~~~~Kotlin
class ExampleHtmlProcessor(private val env: ExecutionEnvironment) : HtmlProcessor {
    override fun beforeHtmlRender(beforeContext: HtmlProcessor.BeforeContext): StepOutput<J2HTMLDOMContent> {
        // Replace the hrefs in all proper anchors
        env.linker.sitePathOf()

        return StepOutput(beforeContext.dom)
    }
}
~~~~

## Környezet

A pluginok megfelelő működéséhez nem feltétlenül csak arra az adott elemre van szükség, amin dolgoznak. Igényelhetik a konfigurációt, amit a felhasználó adott meg, igényelhetnek valami linkelő osztályt és így tovább. Bár ezeket a megfelelő `Context`-en belül is odaadhatnánk nekik, azonban ez nagyon fáradságos lenne (továbbá dependency áramoltatás szempontjából sem szerencsés).

E helyzetet oldja fel az `ExecutionEnvironment` interfész, mely megjelenik az `ExampleHtmlProcessor` konstruktorában is. Ez az interfész felkínál mindent, amit az alkalmazás tud egy plugin számára nyújtani. Egyelőre, csupán példaként, ez a konfigurációt, egy linkert, továbbá egy, a csővezetéket elrejtő interfészt jelent. Nyilván erre a környezetre bármi rápakolható.

## A feldolgozás folyamata

### 0. A feldolgozó osztályok összeállítása

Példányosítjuk az összes plugint, az összes `Stage`-et és az összes `pipeline`-t.

### 1. A feldolgozandó elemek összegyűjtése

Összegyűjtjük az összes kód elemet, az összes lapot (`page`) és az összes assetet (`resource`). Ha ez megvan, akkor fogjuk az összes `LifecycleAware` függvényt, és meghívjuk a `beforeProcessing` metódusukat.

### 2. PipelineItem-ek készítése

Készítünk egy-egy `PipelineItem`-et minden egyes feldolgozandó elemből. Például a kód elemekből egy `CodeElementItem`-et.

### 3. Minden PipelineItem-nek indítunk egy korutint

Minden egyes `PipelineItem` egy saját korutint kap, amin belül az adott elem feldolgozása fog majd történni. Az elemeket a megfelelő pipeline-ra (attól függően, hogy milyen típusúak) egy `PipelineElementVisitor`-ral tehetjük, a visitor tervezési mintát alkalmazva.

### 4. Az elemek végiggördülnek a csővezetékeken

Mindenki szépen végigmegy a neki megfelelő csővezeték összes fázisán.

### 5. Készen vagyunk!

Ennyi is volt :)
