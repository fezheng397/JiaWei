import {
  Badge,
  Button,
  Card,
  CardBody,
  CardFooter,
  CardImage,
  CardStat,
  CardStats,
  Dropdown,
  Field,
  Icon,
  PageLayout,
  Select,
  Shell,
  Tabs,
  Tag,
  Textarea,
} from './design-system'
import './App.css'

function App() {
  return (
    <Shell
      navItems={[
        { href: '#recipes', isCurrent: true, label: 'Recipes' },
        { href: '#ingredients', label: 'Ingredients' },
        { href: '#create', label: 'Create' },
      ]}
    >
      <PageLayout
        action={
          <Button href="#create" variant="primary">
            <Icon name="plus" />
            New recipe
          </Button>
        }
        description="React primitives for rebuilding the Angular recipes experience without changing the visual language."
        title="Design system"
      >
        <Tabs
          defaultValue="recipes"
          items={[
            {
              content: <RecipeShowcase />,
              label: 'Recipes',
              value: 'recipes',
            },
            {
              content: <FormShowcase />,
              label: 'Forms',
              value: 'forms',
            },
            {
              content: <PrimitiveShowcase />,
              label: 'Primitives',
              value: 'primitives',
            },
          ]}
        />
      </PageLayout>
    </Shell>
  )
}

function RecipeShowcase() {
  return (
    <div className="showcase-grid" id="recipes">
      <Card interactive>
        <CardImage>
          <span className="type-image-title">Scallion oil noodles</span>
        </CardImage>
        <CardBody>
          <div>
            <Badge>Recipe</Badge>
            <h2 className="card__title type-card-title">Scallion Oil Noodles</h2>
            <p className="card__description type-body">
              A compact card surface with the same image treatment, serif title, and
              muted supporting copy used by the Angular recipe list.
            </p>
          </div>
          <CardFooter>
            <CardStats>
              <CardStat>
                <Icon name="clock" />
                20 min
              </CardStat>
              <CardStat>
                <Icon name="users" />
                4 servings
              </CardStat>
            </CardStats>
            <p className="type-ui-sm">Jiawei</p>
          </CardFooter>
        </CardBody>
      </Card>

      <Card>
        <CardBody>
          <div className="document-preview">
            <p className="type-meta-label">Recipe document</p>
            <h2 className="type-document-title">Ginger Chicken Congee</h2>
            <p className="type-body-relaxed">
              Document layouts keep generous inner padding, dark step markers, soft
              stat panels, and readable long-form rhythm.
            </p>
          </div>
          <dl className="stat-panel">
            <div>
              <dt className="type-ui-sm">
                <Icon name="clock" />
                Total time
              </dt>
              <dd className="type-stat-value">55 min</dd>
            </div>
            <div>
              <dt className="type-ui-sm">
                <Icon name="leaf" />
                Difficulty
              </dt>
              <dd className="type-stat-value">Gentle</dd>
            </div>
          </dl>
          <ol className="step-list">
            <li>
              <span className="step-number type-step-number">1</span>
              <p>Toast rice, ginger, and aromatics until fragrant.</p>
            </li>
            <li>
              <span className="step-number type-step-number">2</span>
              <p>Simmer until glossy and finish with scallions.</p>
            </li>
          </ol>
        </CardBody>
      </Card>
    </div>
  )
}

function FormShowcase() {
  return (
    <Card>
      <CardBody>
        <div className="form-heading">
          <h2 className="type-section-title">Create recipe basics</h2>
          <Dropdown
            label="Chef filter"
            items={[
              { label: 'Jiawei', value: 'jiawei' },
              { label: 'Honglei', value: 'honglei' },
              { disabled: true, label: 'No chef selected', value: 'none' },
            ]}
            trigger={
              <>
                <Icon name="users" />
                Chef
                <Icon name="chevron-down" />
              </>
            }
          />
        </div>
        <div className="form-grid" id="create">
          <Field defaultValue="Sesame cucumber salad" label="Recipe title" />
          <Select defaultValue="side" label="Category">
            <option value="main">Main</option>
            <option value="side">Side</option>
            <option value="dessert">Dessert</option>
          </Select>
          <Textarea
            defaultValue="Crisp cucumbers, sesame paste, garlic, and a little vinegar."
            label="Description"
          />
          <div className="tag-row" aria-label="Selected tags">
            <Tag>quick</Tag>
            <Tag>vegetarian</Tag>
            <Tag>summer</Tag>
          </div>
        </div>
      </CardBody>
    </Card>
  )
}

function PrimitiveShowcase() {
  return (
    <div className="primitive-stack">
      <Card variant="empty">
        <h2 className="type-section-title">Buttons</h2>
        <div className="button-row">
          <Button variant="primary">Primary</Button>
          <Button variant="secondary">Secondary</Button>
          <Button variant="ghost">Ghost</Button>
          <Button aria-label="Reset filters" variant="icon">
            <Icon name="reset" />
          </Button>
          <Button href="https://jiawei.app" target="_blank" variant="unstyled">
            External link
          </Button>
        </div>
      </Card>

      <Card variant="empty">
        <h2 className="type-section-title">Badges</h2>
        <div className="button-row">
          <Badge>Recipe</Badge>
          <Badge variant="success">Ingredient</Badge>
          <Badge variant="info">New</Badge>
          <Badge variant="danger">Error</Badge>
        </div>
      </Card>
    </div>
  )
}

export default App
